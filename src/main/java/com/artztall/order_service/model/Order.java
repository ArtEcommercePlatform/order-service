package com.artztall.order_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private List<OrderItem> items;
    private double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentStatus paymentStatus;
}
