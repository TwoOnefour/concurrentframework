# 造轮子

实现一个开箱即用的秒杀注解

- 集成redis实现防刷，幂等、快速扣减库存后将订单投递给kafka处理

- TODO:动态拓展集群

# usage

```java

@SeckillWrapper(
        topic = "seckill.order",
        stock = 100,
        stockKey = "product:123:stock"
)
public ResponseResult<Order> order(@RequestBody Order order) {
    this.orderService.order(
            order.getOrderId(),
            order.getProductId(),
            order.getQuantity(),
            order.getUserId()
    );
    return ResponseResult.success(order);
}
```

请见`org.tnf.concurrentframework.example.controller.OrderController`
