package org.tnf.concurrentframework.seckill.core;

public interface SeckillContext {
    /**
     * Get the topic of the current seckill
     *
     * @return the topic of the current seckill
     */
    String getUuid();
    /**
     * Get the user id of the current seckill
     *
     * @return the user id of the current seckill
     */
    String getUserId();
}
