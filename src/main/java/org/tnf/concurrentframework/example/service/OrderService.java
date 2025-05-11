package org.tnf.concurrentframework.example.service;

public interface OrderService {
    void order(String orderId, String productId, int quantity, String userId);


}
