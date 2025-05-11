package org.tnf.concurrentframework.seckill.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController

public class CallbackController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/seckill/callback")
    public ResponseEntity<?> getResult(@RequestParam String taskId) {
        String resultKey = "seckill:result:" + taskId;
        Object result = redisTemplate.opsForValue().get(resultKey);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.PROCESSING)
                    .body(Map.of(
                            "status", "processing",
                            "message", "Request is still being processed"
                    ));
        }

        return ResponseEntity.ok(result);
    }


}
