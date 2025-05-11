package org.tnf.concurrentframework.seckill.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;
import org.tnf.concurrentframework.seckill.core.GenericSeckillContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class SeckillWrapperAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SeckillWrapperAspect(RedisTemplate<String, Object> redisTemplate,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(org.tnf.concurrentframework.seckill.annotation.SeckillWrapper)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SeckillWrapper wrapper = method.getAnnotation(SeckillWrapper.class);

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            throw new IllegalArgumentException("方法必须至少有一个参数");
        }

        // 创建上下文
        GenericSeckillContext context = new GenericSeckillContext();
        context.setUuid(UUID.randomUUID().toString());
        context.setArguments(args);
        context.setSeckillWrapper(wrapper);

        // 从请求体中提取用户ID
        Object firstArg = args[0]; // 假设第一个参数是请求体
        context.setRequestBody(firstArg);
        try {
            String extractorMethodName = wrapper.userIdExtractor();
            Method userIdMethod = firstArg.getClass().getMethod(extractorMethodName);
            Object userId = userIdMethod.invoke(firstArg);
            context.setUserId(userId.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("无法从请求体中提取用户ID，请确保方法 "
                    + wrapper.userIdExtractor() + "() 存在");
        }

        // 设置业务逻辑执行器
        context.setProceedTask(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        // 初始化库存
        String stockKey = getStockKey(wrapper);
        initializeStock(stockKey, wrapper.stock());

        // 用户请求频率限制
        String userKey = "seckill:topic:" + wrapper.topic() + ":user:" + context.getUserId();
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(userKey, "1", Duration.ofSeconds(1)))) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        // 幂等性检查
        if (wrapper.idempotent()) {
            String uuidKey = userKey + ":uuid:" + context.getUuid();
            if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(uuidKey, "1", Duration.ofMinutes(10)))) {
                throw new RuntimeException("重复的订单请求");
            }
        }

        // 扣减库存
        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);
        if (remainingStock == null || remainingStock < 0) {
            // 恢复库存
            redisTemplate.opsForValue().increment(stockKey);
            throw new RuntimeException("商品已售罄");
        }

        // 发送到Kafka
        kafkaTemplate.send(wrapper.topic(), context);

        return buildSeckillResponse(context.getUuid());
    }

    private String getStockKey(SeckillWrapper wrapper) {
        String stockKey = wrapper.stockKey();
        if (stockKey.isEmpty()) {
            stockKey = "seckill:topic:" + wrapper.topic() + ":stock";
        }
        return stockKey;
    }

    private void initializeStock(String stockKey, int stock) {
        // 只有当库存键不存在时才初始化
        if (Boolean.FALSE.equals(redisTemplate.hasKey(stockKey))) {
            redisTemplate.opsForValue().set(stockKey, stock);
        }
    }

    private Map<String, Object> buildSeckillResponse(String taskId) {
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        data.put("callback", "/seckill/callback?taskId=" + taskId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 202);
        response.put("message", "请求已接受，正在处理中");
        response.put("data", data);

        return response;
    }
}