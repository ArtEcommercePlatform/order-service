package com.artztall.order_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDTO {
    private String userId;
    private List<OrderItemDTO> items;
    private String shippingAddress;
}
