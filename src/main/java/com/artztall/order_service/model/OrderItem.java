package com.artztall.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private String artistId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String imageUrl;
    private ProductDimensions dimensions;
    private String medium;
    private String style;
}