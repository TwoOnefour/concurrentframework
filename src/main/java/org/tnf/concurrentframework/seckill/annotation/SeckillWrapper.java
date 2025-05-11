package org.tnf.concurrentframework.seckill.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeckillWrapper {
    // 必需的参数
    String topic();  // Kafka 主题

    // 库存相关参数
    int stock() default 100;  // 默认库存量

    String stockKey() default "";  // Redis中存储库存的键，默认自动生成

    // 高级配置
    boolean idempotent() default true;  // 是否启用幂等性检查

    String userIdExtractor() default "getUserId";  // 从请求体中提取用户ID的方法名
}