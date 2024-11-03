package com.artztall.orderservice.service;

import com.artztall.orderservice.dto.OrderCreateDTO;
import com.artztall.orderservice.dto.OrderResponseDTO;
import com.artztall.orderservice.model.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO);
    OrderResponseDTO getOrder(String orderId);
    List<OrderResponseDTO> getUserOrders(String userId);
    OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status);
    void deleteOrder(String orderId);
}

