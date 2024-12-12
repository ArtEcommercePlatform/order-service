package com.artztall.order_service.controller;



import com.artztall.order_service.dto.OrderCreateDTO;
import com.artztall.order_service.dto.OrderItemDTO;
import com.artztall.order_service.dto.OrderResponseDTO;
import com.artztall.order_service.model.OrderStatus;
import com.artztall.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderCreateDTO orderCreateDTO;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data before each test method
        OrderItemDTO itemDTO = new OrderItemDTO("product123", 2);
        orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setUserId("user123");
        orderCreateDTO.setItem(itemDTO);
        orderCreateDTO.setShippingAddress("123 Test Street");
        orderCreateDTO.setSpecialInstructions("Fragile item");

        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId("order123");
        orderResponseDTO.setUserId("user123");
        orderResponseDTO.setStatus(OrderStatus.PENDING);
        orderResponseDTO.setTotalAmount(BigDecimal.valueOf(100.00));
        orderResponseDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        // Arrange
        when(orderService.createOrder(orderCreateDTO)).thenReturn(orderResponseDTO);

        // Act
        ResponseEntity<OrderResponseDTO> response = orderController.createOrder(orderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).createOrder(orderCreateDTO);
    }

    @Test
    void getOrder_ExistingOrderId_ShouldReturnOrder() {
        // Arrange
        String orderId = "order123";
        when(orderService.getOrder(orderId)).thenReturn(orderResponseDTO);

        // Act
        ResponseEntity<OrderResponseDTO> response = orderController.getOrder(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).getOrder(orderId);
    }

    @Test
    void getUserOrders_ShouldReturnUserOrders() {
        // Arrange
        String userId = "user123";
        List<OrderResponseDTO> userOrders = List.of(orderResponseDTO);
        when(orderService.getUserOrders(userId)).thenReturn(userOrders);

        // Act
        ResponseEntity<List<OrderResponseDTO>> response = orderController.getUserOrders(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userOrders, response.getBody());
        verify(orderService, times(1)).getUserOrders(userId);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        // Arrange
        String orderId = "order123";
        OrderStatus newStatus = OrderStatus.PROCESSING;
        when(orderService.updateOrderStatus(orderId, newStatus)).thenReturn(orderResponseDTO);

        // Act
        ResponseEntity<OrderResponseDTO> response = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).updateOrderStatus(orderId, newStatus);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        // Arrange
        String orderId = "order123";
        doNothing().when(orderService).deleteOrder(orderId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void getOrdersByArtisan_ShouldReturnArtisanOrders() {
        // Arrange
        String artisanId = "artisan123";
        List<OrderResponseDTO> artisanOrders = List.of(orderResponseDTO);
        when(orderService.getOrdersByArtisan(artisanId)).thenReturn(artisanOrders);

        // Act
        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersByArtisan(artisanId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(artisanOrders, response.getBody());
        verify(orderService, times(1)).getOrdersByArtisan(artisanId);
    }
}
