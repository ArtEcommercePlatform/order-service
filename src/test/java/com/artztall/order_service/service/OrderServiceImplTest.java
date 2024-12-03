package com.artztall.order_service.service;

import com.artztall.order_service.dto.ProductAvailabilityRequest;
import com.artztall.order_service.dto.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductClientServiceTest {

    @Mock
    private WebClient productServiceWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private ProductClientService productClientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProduct() {
        ProductResponseDTO mockResponse = new ProductResponseDTO();
        mockResponse.setProductId("123");
        mockResponse.setProductName("Sample Product");

        when(productServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestBodySpec); // Change to correct type
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponseDTO.class)).thenReturn(Mono.just(mockResponse));

        ProductResponseDTO result = productClientService.getProduct("123");

        assertNotNull(result);
        assertEquals("123", result.getProductId());
        assertEquals("Sample Product", result.getProductName());
    }


    @Test
    void testReserveProduct() {
        when(productServiceWebClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(ProductAvailabilityRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponseDTO.class)).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> productClientService.reserveProduct("123"));
    }

    @Test
    void testReleaseProduct() {
        when(productServiceWebClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(ProductAvailabilityRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponseDTO.class)).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> productClientService.releaseProduct("123"));
    }
}
