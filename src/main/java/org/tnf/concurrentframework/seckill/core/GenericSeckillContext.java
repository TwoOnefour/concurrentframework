package org.tnf.concurrentframework.seckill.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenericSeckillContext extends AbstractSeckillContext {
    private String userId;
    private String uuid;
    private Object requestBody;
    private SeckillWrapper seckillWrapper;
    private Object[] arguments;
    private Object result;

    @Override
    public void executeBusinessLogic() {
        if (getProceedTask() != null) {
            result = getProceedTask().get();
        }
    }
}