package com.artztall.orderservice.service.impl;

import com.artztall.orderservice.dto.OrderCreateDTO;
import com.artztall.orderservice.dto.OrderResponseDTO;
import com.artztall.orderservice.model.Order;
import com.artztall.orderservice.model.OrderStatus;
import com.artztall.orderservice.model.PaymentStatus;
import com.artztall.orderservice.repository.OrderRepository;
import com.artztall.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO) {
        Order order = new Order();
        order.setUserId(orderCreateDTO.getUserId());
        order.setItems(mapToOrderItems(orderCreateDTO));
        order.setTotalAmount(calculateTotalAmount(order));
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddress(orderCreateDTO.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponseDTO> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToOrderResponse(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    // Helper methods for mapping and calculations
    // (Implementation details omitted for brevity but would include proper mapping logic)
}


