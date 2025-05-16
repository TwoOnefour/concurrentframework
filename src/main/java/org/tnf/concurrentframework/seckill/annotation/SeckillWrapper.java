package org.tnf.concurrentframework.seckill.annotation;

import org.tnf.concurrentframework.seckill.core.AbstractSeckillContext;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeckillWrapper {
    String topic();

    int capacity() default 100;

    int refillRate() default 10;

    int stock();

    String stockUUID();

    boolean idempotent() default true;

    Class<?> paramType() default AbstractSeckillContext.class;
}