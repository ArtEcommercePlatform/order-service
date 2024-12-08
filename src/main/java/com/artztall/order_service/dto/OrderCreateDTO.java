package com.artztall.order_service.dto;


import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDTO {
    private String userId;
    private OrderItemDTO item;
    private String shippingAddress;
    private String specialInstructions;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderItemDTO getItem() {
        return item;
    }

    public void setItem(OrderItemDTO item) {
        this.item = item;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }


}