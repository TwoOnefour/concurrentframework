package org.tnf.concurrentframework.seckill.core;

public interface SeckillContext {
    /**
     * Get the topic of the current seckill
     *
     * @return the topic of the current seckill
     */
    String seckillGetUuid();
    /**
     * Get the user id of the current seckill
     *
     * @return the user id of the current seckill
     */
    String seckillGetUserId();

    Runnable getProceedTask();

    void setProceedTask(Runnable task);

    default Object executeBusinessLogic() throws Exception {
        if (getProceedTask() != null) {
            getProceedTask().run();
        }
        return null;
    }

}
