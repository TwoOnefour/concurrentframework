package org.tnf.concurrentframework.seckill.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;
import org.tnf.concurrentframework.seckill.core.RedisTokenBucket;
import org.tnf.concurrentframework.seckill.core.SeckillContext;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class SeckillWrapperAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ConcurrentHashMap<String, RedisTokenBucket> bucketCache = new ConcurrentHashMap<>();

    SeckillWrapperAspect(RedisTemplate<String, Object> redisTemplate,
                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;

    }

    @Around("@annotation(wrapper)")
    public Object around(ProceedingJoinPoint joinPoint, SeckillWrapper wrapper) {
        Object[] args = joinPoint.getArgs();
        Class<?> paramType = wrapper.paramType();

        SeckillContext context = null;
        for (Object arg : args) {
            if (paramType.isInstance(arg)) {
                context = (SeckillContext) arg;
                break;
            }
        }

        if (context == null) {
            throw new IllegalArgumentException("Request body must be of type " + paramType.getName());
        }

        String stockKey = "seckill:topic:" + wrapper.topic() + "stock:" + wrapper.stockUUID();
        // for user

        String userKey = "seckill:topic:" + wrapper.topic() + "user:" + context.seckillGetUserId();
        // for idempotent
        String uuidKey = userKey + ":uuid:" + context.seckillGetUuid();


        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(userKey, "1", Duration.ofSeconds(1)))) {
            throw new RuntimeException("Request too frequent");
        }

        if (wrapper.idempotent()) {

            if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(uuidKey, "1", Duration.ofMinutes(10)))) {
                throw new RuntimeException("Duplicate order request");
            }
        }


        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);
        if (remainingStock < 0) {
            throw new RuntimeException("Stock is sold out");
        }
        // process success order by kafka.
        context.setProceedTask(() -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        kafkaTemplate.send(wrapper.topic(), context);

        return context;
    }
}