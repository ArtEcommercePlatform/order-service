package com.artztall.order_service.service;


import com.artztall.order_service.dto.OrderCreateDTO;
import com.artztall.order_service.dto.OrderResponseDTO;
import com.artztall.order_service.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO);
    OrderResponseDTO getOrder(String orderId);
    List<OrderResponseDTO> getUserOrders(String userId);
    OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status);
    void deleteOrder(String orderId);
}

