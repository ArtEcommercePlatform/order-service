package com.artztall.order_service.model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private String artistId;
}
