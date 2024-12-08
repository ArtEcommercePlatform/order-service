package com.artztall.order_service.dto;


import lombok.Data;


@Data
public class OrderItemDTO {
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private String productId;
    private int quantity;
}