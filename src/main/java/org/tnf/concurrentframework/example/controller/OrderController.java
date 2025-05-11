package org.tnf.concurrentframework.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tnf.concurrentframework.example.entity.ResponseResult;
import org.tnf.concurrentframework.example.model.Order;
import org.tnf.concurrentframework.example.service.OrderService;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;


@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("doOrder")
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
}
