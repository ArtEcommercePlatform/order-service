package com.artztall.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDimensionsDTO {
    private Double length;
    private Double width;
    private String unit;
}
