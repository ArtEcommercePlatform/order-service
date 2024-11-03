package com.artztall.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new order")
public class OrderCreateDTO {
    @NotBlank(message = "User ID is required")
    @Schema(description = "ID of the user placing the order", example = "user123", required = true)
    private String userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @Schema(description = "List of items in the order", required = true)
    private List<OrderItemDTO> items;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    @Schema(description = "Delivery address for the order",
            example = "123 Main St, Apt 4B, New York, NY 10001",
            required = true)
    private String shippingAddress;
}
