package org.tnf.concurrentframework.seckill.core;

import java.util.function.Supplier;

public abstract class AbstractSeckillContext implements SeckillContext {
    private Supplier<Object> proceedTask;

    @Override
    public Supplier<Object> getProceedTask() {
        return this.proceedTask;
    }

    @Override
    public void setProceedTask(Supplier<Object> task) {
        this.proceedTask = task;
    }
}