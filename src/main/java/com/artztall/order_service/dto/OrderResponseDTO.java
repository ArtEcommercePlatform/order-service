package com.artztall.order_service.dto;
import com.artztall.orderservice.model.OrderStatus;
import com.artztall.orderservice.model.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private String id;
    private String userId;
    private List<OrderItemDTO> items;
    private double totalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
