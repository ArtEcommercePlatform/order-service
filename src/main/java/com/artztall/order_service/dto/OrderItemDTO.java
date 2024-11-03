package com.artztall.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for order item details")
public class OrderItemDTO {
    @NotBlank(message = "Product ID is required")
    @Schema(description = "ID of the product", example = "prod123", required = true)
    private String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    @Schema(description = "Quantity of the product",
            example = "2",
            minimum = "1",
            maximum = "100",
            required = true)
    private int quantity;
}
