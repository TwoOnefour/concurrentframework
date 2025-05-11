package org.tnf.concurrentframework.example.dao;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;
import org.tnf.concurrentframework.example.model.Order;

import java.util.List;

@Component
public interface OrderMapper {
    @Insert(
            "INSERT INTO orders (order_id, product_id, quantity, user_id) " +
                    "VALUES " +
                    "(#{orderId}, #{productId}, #{quantity}, #{userId})"
    )
    boolean insertOrder(String orderId, String productId, int quantity, String userId);

    @Insert(
            "INSERT INTO orders (order_id, product_id, quantity, user_id) " +
                    "VALUES " +
                    "<foreach collection='orders' item='order' separator=','>" +
                    "(#{order.orderId}, #{order.productId}, #{order.quantity}, #{order.userId})" +
                    "</foreach>"
    )
    boolean batchInsertOrder(List<Order> orders);
}
