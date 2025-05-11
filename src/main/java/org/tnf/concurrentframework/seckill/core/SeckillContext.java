package org.tnf.concurrentframework.seckill.core;

import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;

import java.util.function.Supplier;

public interface SeckillContext {
    String getUserId();

    void setUserId(String userId);

    String getUuid();

    void setUuid(String uuid);

    Supplier<Object> getProceedTask();

    void setProceedTask(Supplier<Object> task);

    SeckillWrapper getSeckillWrapper();

    void setSeckillWrapper(SeckillWrapper seckillWrapper);

    void executeBusinessLogic();
}