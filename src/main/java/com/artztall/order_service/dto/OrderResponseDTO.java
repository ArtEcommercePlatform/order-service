package com.artztall.order_service.dto;

import com.artztall.order_service.model.OrderStatus;
import com.artztall.order_service.model.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for order response")
public class OrderResponseDTO {
    @Schema(description = "Unique identifier of the order", example = "ord123")
    private String id;

    @Schema(description = "ID of the user who placed the order", example = "user123")
    private String userId;

    @Schema(description = "List of items in the order")
    private List<OrderItemDTO> items;

    @Schema(description = "Current status of the order",
            example = "PENDING",
            allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    private OrderStatus status;

    @Schema(description = "Current payment status",
            example = "PENDING",
            allowableValues = {"PENDING", "PAID", "FAILED", "REFUNDED"})
    private PaymentStatus paymentStatus;

    @Schema(description = "Delivery address for the order",
            example = "123 Main St, Apt 4B, New York, NY 10001")
    private String shippingAddress;

    @Schema(description = "Timestamp when the order was created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the order was last updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
