package com.artztall.order_service.service;

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
}