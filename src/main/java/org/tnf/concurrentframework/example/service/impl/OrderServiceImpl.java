package org.tnf.concurrentframework.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.tnf.concurrentframework.example.dao.OrderMapper;
import org.tnf.concurrentframework.example.service.OrderService;


@Log4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public void order(String orderId, String productId, int quantity, String userId) {
        orderMapper.insertOrder(orderId, productId, quantity, userId);
        logger.info("{} inserted {} into {}", orderId, productId, userId);

    }
}
