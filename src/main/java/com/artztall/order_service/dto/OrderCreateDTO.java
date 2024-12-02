package com.artztall.order_service.dto;


import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDTO {
    private String userId;
    private OrderItemDTO item;
    private String shippingAddress;
    private String specialInstructions;
}