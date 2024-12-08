package com.artztall.order_service.dto;

import lombok.Data;

@Data
public class ProductDimensionsDTO {
    private Double length;

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private Double width;
    private String unit;
}
