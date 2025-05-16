package org.tnf.concurrentframework.seckill.core;

public interface SeckillContext {
    /**
     * Get the skull order id of the current seckill
     *
     * @return the skull order id of the current seckill
     */
    String seckillGetUuid();

    /**
     * Get the user id of the current seckill
     *
     * @return the user id of the current seckill
     */
    String seckillGetUserId();

    default Object executeBusinessLogic() throws Exception {
        if (getProceedTask() != null) {
            getProceedTask().run();
        }
        return null;
    }

    Runnable getProceedTask();

    void setProceedTask(Runnable task);

    String getItemId();
}
