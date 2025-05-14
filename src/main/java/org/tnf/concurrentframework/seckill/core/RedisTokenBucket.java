package org.tnf.concurrentframework.seckill.core;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Objects;

public class RedisTokenBucket {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String key;
    private final int capacity;
    private final int refillRate;

    public RedisTokenBucket(RedisTemplate<String, Object> redisTemplate, String key, int capacity, int refillRate) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public boolean tryConsume() {
        Long tokens = redisTemplate.opsForValue().decrement(key);
        if (tokens == null || tokens < 0) {
            redisTemplate.opsForValue().increment(key);
            return false;
        }
        return true;
    }

    public void refill() {
        redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(capacity), Duration.ofMinutes(1));
        redisTemplate.opsForValue().increment(key, refillRate);
        int redisCapacity = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString());
        if (redisCapacity > capacity) {
            redisTemplate.opsForValue().set(key, String.valueOf(capacity));
        }
    }
}