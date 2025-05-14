package org.tnf.concurrentframework.seckill.core;

public abstract class AbstractSeckillContext implements SeckillContext {
    private Runnable proceedTask;

    @Override
    public void setProceedTask(Runnable task) {
        this.proceedTask = task;
    }

    @Override
    public Runnable getProceedTask() {
        return this.proceedTask;
    }

}