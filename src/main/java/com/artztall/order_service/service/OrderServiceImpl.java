package com.artztall.order_service.service;

import com.artztall.order_service.dto.*;
import com.artztall.order_service.model.*;
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
    private final ProductClientService productClientService;
    private final NotificationClientService notificationClientService;

    @Override
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO) {
        // Validate all products exist and are available
        validateProducts(orderCreateDTO.getItems());

        Order order = new Order();
        order.setUserId(orderCreateDTO.getUserId());
        order.setItems(mapToOrderItems(orderCreateDTO));
        order.setTotalAmount(calculateTotalAmount(order));
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddress(orderCreateDTO.getShippingAddress());
        order.setSpecialInstructions(orderCreateDTO.getSpecialInstructions());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        NotificationSendDTO notification = new NotificationSendDTO();
        notification.setUserId(savedOrder.getUserId());
        notification.setMessage("Your order #" + savedOrder.getId() + " has been successfully placed.");
        notification.setType("INFO");
        notification.setActionUrl("http://localhost:5173");
        notificationClientService.sendNotification(notification);

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
        Order updatedOrder = orderRepository.save(order);

        // Send order status update notification
        NotificationSendDTO notification = new NotificationSendDTO();
        notification.setUserId(updatedOrder.getUserId());
        notification.setType("INFO");
        notification.setMessage("Your order #" + updatedOrder.getId() + " status has been updated to " + status);
        notificationClientService.sendNotification(notification);

        return mapToOrderResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    private void validateProducts(List<OrderItemDTO> items) {
        items.forEach(item -> {
            ProductResponseDTO product = productClientService.getProduct(item.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getProductId());
            }
            if (!product.isAvailable()) {
                throw new RuntimeException("Product is not available: " + item.getProductId());
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductId());
            }
        });
    }

    private List<OrderItem> mapToOrderItems(OrderCreateDTO orderCreateDTO) {
        return orderCreateDTO.getItems().stream()
                .map(this::mapToOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItem mapToOrderItem(OrderItemDTO itemDTO) {
        OrderItem orderItem = new OrderItem();
        ProductResponseDTO product = productClientService.getProduct(itemDTO.getProductId());

        orderItem.setProductId(itemDTO.getProductId());
        orderItem.setProductName(product.getName());
        orderItem.setArtistId(product.getArtistId());
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        orderItem.setImageUrl(product.getImageUrl());
        orderItem.setDimensions(mapProductDimensions(product.getDimensions()));
        orderItem.setMedium(product.getMedium());
        orderItem.setStyle(product.getStyle());

        return orderItem;
    }

    private ProductDimensions mapProductDimensions(ProductDimensionsDTO dimensionsDTO) {
        if (dimensionsDTO == null) return null;

        ProductDimensions dimensions = new ProductDimensions();
        dimensions.setLength(dimensionsDTO.getLength());
        dimensions.setWidth(dimensionsDTO.getWidth());
        dimensions.setUnit(dimensionsDTO.getUnit());
        return dimensions;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponseDTO mapToOrderResponse(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setUserId(order.getUserId());
        responseDTO.setItems(mapToOrderItemResponses(order.getItems()));
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setPaymentStatus(order.getPaymentStatus());
        responseDTO.setShippingAddress(order.getShippingAddress());
        responseDTO.setSpecialInstructions(order.getSpecialInstructions());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setUpdatedAt(order.getUpdatedAt());
        return responseDTO;
    }

    private List<OrderItemResponseDTO> mapToOrderItemResponses(List<OrderItem> items) {
        return items.stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
    }

    private OrderItemResponseDTO mapToOrderItemResponse(OrderItem item) {
        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
        itemDTO.setProductId(item.getProductId());
        itemDTO.setProductName(item.getProductName());
        itemDTO.setArtistId(item.getArtistId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setSubtotal(item.getSubtotal());
        itemDTO.setImageUrl(item.getImageUrl());
        itemDTO.setDimensions(item.getDimensions());
        itemDTO.setMedium(item.getMedium());
        itemDTO.setStyle(item.getStyle());
        return itemDTO;
    }
}