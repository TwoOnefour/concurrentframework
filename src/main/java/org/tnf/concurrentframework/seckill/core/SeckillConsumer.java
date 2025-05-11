package org.tnf.concurrentframework.seckill.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class SeckillConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "#{'${seckill.topics}'.split(',')}")
    public void processSeckill(GenericSeckillContext context) {
        String stockKey = getStockKey(context);
        String resultKey = "seckill:topic:" + context.getSeckillWrapper().topic() +
                ":user:" + context.getUserId() +
                ":uuid:" + context.getUuid() +
                ":result";

        try {
            // 执行业务逻辑
            context.executeBusinessLogic();

            // 记录成功结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("timestamp", System.currentTimeMillis());
            result.put("data", context.getResult());
            redisTemplate.opsForValue().set(resultKey, result, Duration.ofMinutes(30));
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            redisTemplate.opsForValue().set(resultKey, result, Duration.ofMinutes(30));
            redisTemplate.opsForValue().increment(stockKey);
        }
    }

    private String getStockKey(GenericSeckillContext context) {
        SeckillWrapper wrapper = context.getSeckillWrapper();
        String stockKey = wrapper.stockKey();
        if (stockKey.isEmpty()) {
            stockKey = "seckill:topic:" + wrapper.topic() + ":stock";
        }
        return stockKey;
    }
}