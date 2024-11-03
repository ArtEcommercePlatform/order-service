package com.artztall.order_service.service;

import com.artztall.order_service.dto.OrderCreateDTO;
import com.artztall.order_service.dto.OrderResponseDTO;
import com.artztall.order_service.dto.OrderItemDTO;
import com.artztall.order_service.model.Order;
import com.artztall.order_service.model.OrderItem;
import com.artztall.order_service.model.OrderStatus;
import com.artztall.order_service.model.PaymentStatus;
import com.artztall.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

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
    private List<OrderItem> mapToOrderItems(OrderCreateDTO orderCreateDTO) {
        return orderCreateDTO.getItems().stream()
                .map(this::mapToOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItem mapToOrderItem(OrderItemDTO itemDTO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(itemDTO.getProductId());
        orderItem.setQuantity(itemDTO.getQuantity());
        // Note: In a real implementation, you might want to fetch the product price
        // from a product service and set it here
        return orderItem;
    }

    private OrderResponseDTO mapToOrderResponse(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setUserId(order.getUserId());
        responseDTO.setItems(mapToOrderItemDTOs(order.getItems()));
        responseDTO.setStatus(order.getStatus());
        responseDTO.setPaymentStatus(order.getPaymentStatus());
        responseDTO.setShippingAddress(order.getShippingAddress());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setUpdatedAt(order.getUpdatedAt());
        return responseDTO;
    }

    private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items) {
        return items.stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList());
    }

    private OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item.getProductId());
        itemDTO.setQuantity(item.getQuantity());
        return itemDTO;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getItems().stream()
                .map(item -> {
                    // In a real implementation, you would:
                    // 1. Get the product price from a product service
                    // 2. Multiply by quantity
                    // 3. Apply any discounts or promotions
                    // For now, we'll return a dummy calculation
                    return BigDecimal.valueOf(item.getQuantity() * 10.0);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}