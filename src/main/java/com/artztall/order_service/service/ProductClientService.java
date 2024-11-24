package com.artztall.order_service.service;

import com.artztall.order_service.dto.ProductAvailabilityRequest;
import com.artztall.order_service.dto.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class ProductClientService {
    private final WebClient productServiceWebClient;

    public ProductResponseDTO getProduct(String productId) {
        return productServiceWebClient.get()
                .uri("/api/products/" + productId)
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }

    public void reserveProduct(String productId) {
        productServiceWebClient.put()
                .uri("/api/products/" + productId + "/reserve")
                .bodyValue(new ProductAvailabilityRequest(false))
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }

    public void releaseProduct(String productId) {
        productServiceWebClient.put()
                .uri("/api/products/" + productId + "/release")
                .bodyValue(new ProductAvailabilityRequest(true))
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }
}