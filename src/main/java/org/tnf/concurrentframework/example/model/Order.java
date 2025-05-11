package org.tnf.concurrentframework.example.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Order {
    @NotNull
    String orderId;
    @NotNull
    String productId;
    @NotNull
    int quantity;
    @NotNull
    String userId;

    String productName;
}
