package com.artztall.order_service.service;

import com.artztall.order_service.dto.*;
import com.artztall.order_service.model.*;
import com.artztall.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClientService productClientService;

    @Mock
    private NotificationClientService notificationClientService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderCreateDTO orderCreateDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    public void setUp() {
        // Setup mock product
        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId("product123");
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(BigDecimal.valueOf(100.00));
        productResponseDTO.setAvailable(true);
        productResponseDTO.setStockQuantity(10);
        productResponseDTO.setArtistId("artist123");
        productResponseDTO.setImageUrl("http://example.com/image.jpg");
        productResponseDTO.setDimensions(new ProductDimensionsDTO(10.0, 20.0, "cm"));
        productResponseDTO.setMedium("Canvas");
        productResponseDTO.setStyle("Modern");

        // Setup order create DTO
        orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setUserId("user123");
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId("product123");
        itemDTO.setQuantity(1);
        orderCreateDTO.setItem(itemDTO);
        orderCreateDTO.setShippingAddress("123 Test St");
        orderCreateDTO.setSpecialInstructions("Handle with care");
    }

    @Test
    public void createOrder_Success() {
        // Mock product service
        when(productClientService.getProduct("product123")).thenReturn(productResponseDTO);

        // Mock product reservation
        doNothing().when(productClientService).reserveProduct("product123");

        // Mock notification service
        doNothing().when(notificationClientService).sendNotification(any());

        // Prepare mock saved order
        Order savedOrder = new Order();
        savedOrder.setId("order123");
        savedOrder.setUserId("user123");
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setPaymentStatus(PaymentStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId("product123");
        orderItem.setProductName("Test Product");
        orderItem.setQuantity(1);
        orderItem.setPrice(BigDecimal.valueOf(100.00));
        orderItem.setSubtotal(BigDecimal.valueOf(100.00));
        orderItem.setImageUrl("http://example.com/image.jpg");
        orderItem.setDimensions(new ProductDimensions(10.0, 20.0, "cm"));
        orderItem.setMedium("Canvas");
        orderItem.setStyle("Modern");
        savedOrder.setItem(orderItem);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Execute method
        OrderResponseDTO response = orderService.createOrder(orderCreateDTO);

        // Verify response
        assertNotNull(response);
        assertEquals("order123", response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        // Verify order item details
        assertNotNull(response.getItem());
        assertEquals("product123", response.getItem().getProductId());
        assertEquals("Test Product", response.getItem().getProductName());
        assertEquals(1, response.getItem().getQuantity());
        assertEquals(BigDecimal.valueOf(100.00), response.getItem().getPrice());
        assertEquals(BigDecimal.valueOf(100.00), response.getItem().getSubtotal());

        // Verify interactions
        verify(productClientService, times(2)).getProduct("product123");
        verify(productClientService).reserveProduct("product123");
        verify(orderRepository).save(any(Order.class));
        verify(notificationClientService).sendNotification(any());
    }


    @Test
    public void createOrder_ProductNotAvailable() {
        // Setup unavailable product
        productResponseDTO.setAvailable(false);
        when(productClientService.getProduct(anyString())).thenReturn(productResponseDTO);

        // Expect exception
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderCreateDTO);
        });
    }

    @Test
    public void getOrder_Success() {
        // Prepare mock order
        Order mockOrder = new Order();
        mockOrder.setId("order123");
        mockOrder.setUserId("user123");

        // Create and set OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId("product123");
        orderItem.setQuantity(1);
        mockOrder.setItem(orderItem);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(mockOrder));

        // Execute method
        OrderResponseDTO response = orderService.getOrder("order123");

        // Verify
        assertNotNull(response);
        assertEquals("order123", response.getId());
        assertEquals("user123", response.getUserId());
    }

    @Test
    public void updateOrderStatus_Success() {
        // Prepare mock order
        Order existingOrder = new Order();
        existingOrder.setId("order123");
        existingOrder.setUserId("user123");
        existingOrder.setStatus(OrderStatus.PENDING);

        // Ensure OrderItem is not null
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId("product123");
        orderItem.setQuantity(1);
        existingOrder.setItem(orderItem);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(existingOrder));

        // Mock notification service
        doNothing().when(notificationClientService).sendNotification(any());

        // Mock order save
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Execute method
        OrderResponseDTO response = orderService.updateOrderStatus("order123", OrderStatus.CONFIRMED);

        // Verify
        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals(PaymentStatus.COMPLETED, response.getPaymentStatus());

        // Verify interactions
        verify(orderRepository).save(any(Order.class));
        verify(notificationClientService).sendNotification(any());
    }


    @Test
    public void deleteOrder_Success() {
        // Prepare mock order
        Order existingOrder = new Order();
        existingOrder.setId("order123");
        existingOrder.setItem(new OrderItem());
        existingOrder.getItem().setProductId("product123");

        when(orderRepository.findById("order123")).thenReturn(Optional.of(existingOrder));

        // Mock product release
        doNothing().when(productClientService).releaseProduct(anyString());

        // Execute method
        assertDoesNotThrow(() -> orderService.deleteOrder("order123"));

        // Verify interactions
        verify(productClientService).releaseProduct(anyString());
        verify(orderRepository).deleteById("order123");
    }

    @Test
    public void releaseAbandonedOrders_Success() {
        // Prepare abandoned orders
        Order abandonedOrder = new Order();
        abandonedOrder.setId("order123");
        abandonedOrder.setUserId("user123");
        abandonedOrder.setStatus(OrderStatus.PENDING);
        abandonedOrder.setItem(new OrderItem());
        abandonedOrder.getItem().setProductId("product123");
        abandonedOrder.setCreatedAt(LocalDateTime.now().minusMinutes(20));

        // Mock repository to return abandoned orders
        when(orderRepository.findByStatusAndCreatedAtBefore(
                eq(OrderStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(abandonedOrder));

        // Mock product release
        doNothing().when(productClientService).releaseProduct(anyString());

        // Mock notification service
        doNothing().when(notificationClientService).sendNotification(any());

        // Execute method
        orderService.releaseAbandonedOrders();

        // Verify interactions
        verify(productClientService).releaseProduct(anyString());
        verify(orderRepository).save(abandonedOrder);
        verify(notificationClientService).sendNotification(any());
    }
}