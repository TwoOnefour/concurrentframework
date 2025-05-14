package org.tnf.concurrentframework.seckill.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;
import org.tnf.concurrentframework.seckill.core.SeckillContext;
import org.tnf.concurrentframework.seckill.core.RedisTokenBucket;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class SeckillWrapperAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ConcurrentHashMap<String, RedisTokenBucket> bucketCache = new ConcurrentHashMap<>();

    @Around("@annotation(wrapper)")
    public Object around(ProceedingJoinPoint joinPoint, SeckillWrapper wrapper) {
        Object arg = joinPoint.getArgs()[0];

        if (!(arg instanceof SeckillContext)) {
            throw new IllegalArgumentException("Request body must implement SeckillContext");
        }

        SeckillContext context = (SeckillContext) arg;
        RedisTokenBucket tokenBucket = bucketCache.computeIfAbsent(wrapper.topic(),
                k -> new RedisTokenBucket(redisTemplate, k, wrapper.capacity(), wrapper.refillRate()));

        if (!tokenBucket.tryConsume()) {
            throw new RuntimeException("Rate limit exceeded");
        }

        if (wrapper.idempotent()) {
            String uuidKey = "seckill:uuid:" + context.getUuid();
            if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(uuidKey, "1", Duration.ofMinutes(10)))) {
                throw new RuntimeException("Duplicate order request");
            }
        }

        String userKey = "seckill:user:" + context.getUserId();
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(userKey, "1", Duration.ofSeconds(1)))) {
            throw new RuntimeException("Request too frequent");
        }

        kafkaTemplate.send(wrapper.topic(), context);

        return context;
    }

    SeckillWrapperAspect(RedisTemplate<String, Object> redisTemplate,
                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;

    }
}