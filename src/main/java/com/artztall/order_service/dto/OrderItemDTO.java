package com.artztall.order_service.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productId;
    private int quantity;
}

