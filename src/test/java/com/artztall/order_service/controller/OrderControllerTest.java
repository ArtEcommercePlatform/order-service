package com.artztall.order_service.controller;

import com.artztall.order_service.dto.OrderCreateDTO;
import com.artztall.order_service.dto.OrderResponseDTO;
import com.artztall.order_service.model.OrderStatus;
import com.artztall.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        when(orderService.createOrder(orderCreateDTO)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.createOrder(orderCreateDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).createOrder(orderCreateDTO);
    }

    @Test
    void testGetOrder() {
        String orderId = "123";
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        when(orderService.getOrder(orderId)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).getOrder(orderId);
    }

    @Test
    void testGetUserOrders() {
        String userId = "user1";
        List<OrderResponseDTO> orders = Arrays.asList(new OrderResponseDTO(), new OrderResponseDTO());

        when(orderService.getUserOrders(userId)).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getUserOrders(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).getUserOrders(userId);
    }

    @Test
    void testUpdateOrderStatus() {
        String orderId = "123";
        OrderStatus status = OrderStatus.CONFIRMED;
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        when(orderService.updateOrderStatus(orderId, status)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrderStatus(orderId, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).updateOrderStatus(orderId, status);
    }

    @Test
    void testDeleteOrder() {
        String orderId = "123";

        doNothing().when(orderService).deleteOrder(orderId);

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void testGetOrdersByArtisan() {
        String artisanId = "artisan1";
        List<OrderResponseDTO> artisanOrders = Arrays.asList(new OrderResponseDTO(), new OrderResponseDTO());

        when(orderService.getOrdersByArtisan(artisanId)).thenReturn(artisanOrders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersByArtisan(artisanId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(artisanOrders, response.getBody());
        verify(orderService, times(1)).getOrdersByArtisan(artisanId);
    }
}
