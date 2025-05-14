package org.tnf.concurrentframework.seckill.annotation.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.tnf.concurrentframework.seckill.annotation.SeckillHandler;
import org.tnf.concurrentframework.seckill.ratelimit.RedisTokenBucket;

public class SeckillHandlerImpl implements SeckillHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTokenBucket redisTokenBucket;

    SeckillHandlerImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTokenBucket = new RedisTokenBucket(redisTemplate, "seckill:token", 100, 10);
    }

    @Override
    public void handler(wo) {
        if (!redisTokenBucket.tryConsume()) {
            throw new RuntimeException("Rate limit exceeded");
        }

        if ()
    }


}
