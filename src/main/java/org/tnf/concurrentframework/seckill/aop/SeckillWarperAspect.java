package org.tnf.concurrentframework.seckill.aop;


import org.springframework.stereotype.Component;

@Aspect
@Component
public class SeckillWrapperAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Around("@annotation(wrapper)")
    public Object around(ProceedingJoinPoint joinPoint, SeckillWrapper wrapper) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Object[] args = joinPoint.getArgs();
        OrderContext context = (OrderContext) args[0];

        // 令牌桶限流
        RedisTokenBucket tokenBucket = new RedisTokenBucket(redisTemplate, "seckill:token", wrapper.capacity(), wrapper.refillRate());
        if (!tokenBucket.tryConsume()) {
            throw new RuntimeException("Rate limit exceeded");
        }

        // 幂等性检查
        if (wrapper.idempotent()) {
            String uuidKey = "seckill:uuid:" + context.getUuid();
            if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(uuidKey, "1", Duration.ofMinutes(10)))) {
                throw new RuntimeException("Duplicate order request");
            }
        }

        // Redis 限频（用户防刷）
        String userKey = "seckill:user:" + context.getUserId();
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(userKey, "1", Duration.ofSeconds(1)))) {
            throw new RuntimeException("Request too frequent");
        }

        // 投递 Kafka
        kafkaTemplate.send(wrapper.topic(), context);

        return context; // 可返回上下文对象，视接口定义
    }
}