package com.artztall.order_service.service;

import com.artztall.order_service.dto.*;
import com.artztall.order_service.model.*;
import com.artztall.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductClientService productClientService;
    private final NotificationClientService notificationClientService;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO) {
        log.info("Creating order for user: {}", orderCreateDTO.getUserId());

        // Validate the single product exists and is available
        validateProduct(orderCreateDTO.getItem());

        try {
            // Reserve the product
            try {
                productClientService.reserveProduct(orderCreateDTO.getItem().getProductId());
                log.info("Reserved product: {}", orderCreateDTO.getItem().getProductId());
            } catch (Exception e) {
                log.error("Failed to reserve product: {}", orderCreateDTO.getItem().getProductId(), e);
                throw new RuntimeException("Failed to reserve product: " + orderCreateDTO.getItem().getProductId());
            }

            Order order = new Order();
            order.setUserId(orderCreateDTO.getUserId());
            order.setItem(mapToOrderItem(orderCreateDTO));
            order.setTotalAmount(calculateTotalAmount(order));
            order.setStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setShippingAddress(orderCreateDTO.getShippingAddress());
            order.setSpecialInstructions(orderCreateDTO.getSpecialInstructions());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(order);

            // Send notification
            NotificationSendDTO notification = new NotificationSendDTO();
            notification.setUserId(savedOrder.getUserId());
            notification.setMessage("Your order #" + savedOrder.getId() + " has been successfully placed. Please complete payment within 15 minutes.");
            notification.setType("INFO");
            notification.setActionUrl("http://localhost:5173/payment/" + savedOrder.getId());
            notificationClientService.sendNotification(notification);

            log.info("Order created successfully: {}", savedOrder.getId());
            return mapToOrderResponse(savedOrder);

        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
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
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status) {
        log.info("Updating order status: {} to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        if(status == OrderStatus.CONFIRMED){
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }else if(status == OrderStatus.EXPIRED){
            order.setPaymentStatus(PaymentStatus.FAILED);
        }
        Order updatedOrder = orderRepository.save(order);

        // If order is cancelled or expired, release the product
        if (status == OrderStatus.CANCELLED || status == OrderStatus.EXPIRED) {
            releaseOrderProduct(order);
        }

        // Send notification
        NotificationSendDTO notification = new NotificationSendDTO();
        notification.setUserId(updatedOrder.getUserId());
        notification.setType("INFO");
        notification.setMessage("Your order #" + updatedOrder.getId() + " status has been updated to " + status);
        notification.setActionUrl("http://localhost:5173/orders/" + updatedOrder.getId());
        notificationClientService.sendNotification(notification);

        log.info("Order status updated successfully: {}", orderId);
        return mapToOrderResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Release product before deleting
        releaseOrderProduct(order);
        orderRepository.deleteById(orderId);
        log.info("Order deleted successfully: {}", orderId);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByArtisan(String artisanId) {
        log.info("Fetching confirmed orders for artisan: {}", artisanId);

        return orderRepository.findByItem_ArtistId(artisanId).stream()
                .filter(order -> PaymentStatus.COMPLETED.equals(order.getPaymentStatus())) // Filter by paymentStatus
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseAbandonedOrders() {
        log.info("Checking for abandoned orders...");

        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);
        List<Order> abandonedOrders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PENDING,
                expirationTime
        );

        for (Order order : abandonedOrders) {
            try {
                releaseOrderProduct(order);
                order.setStatus(OrderStatus.EXPIRED);
                orderRepository.save(order);

                // Send notification
                NotificationSendDTO notification = new NotificationSendDTO();
                notification.setUserId(order.getUserId());
                notification.setMessage("Your order #" + order.getId() + " has expired due to incomplete payment.");
                notification.setType("WARNING");
                notification.setActionUrl("http://localhost:5173/orders/" + order.getId());
                notificationClientService.sendNotification(notification);

                log.info("Released abandoned order: {}", order.getId());
            } catch (Exception e) {
                log.error("Failed to process abandoned order: {}", order.getId(), e);
            }
        }
    }

    private void validateProduct(OrderItemDTO item) {
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
    }

    public void releaseOrderProduct(Order order) {
        try {
            productClientService.releaseProduct(order.getItem().getProductId());
            log.info("Released product: {}", order.getItem().getProductId());
        } catch (Exception e) {
            log.error("Failed to release product: {}", order.getItem().getProductId(), e);
        }
    }

    public OrderItem mapToOrderItem(OrderCreateDTO orderCreateDTO) {
        OrderItem orderItem = new OrderItem();
        ProductResponseDTO product = productClientService.getProduct(orderCreateDTO.getItem().getProductId());

        orderItem.setProductId(orderCreateDTO.getItem().getProductId());
        orderItem.setProductName(product.getName());
        orderItem.setArtistId(product.getArtistId());
        orderItem.setQuantity(orderCreateDTO.getItem().getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(orderCreateDTO.getItem().getQuantity())));
        orderItem.setImageUrl(product.getImageUrl());
        orderItem.setDimensions(mapProductDimensions(product.getDimensions()));
        orderItem.setMedium(product.getMedium());
        orderItem.setStyle(product.getStyle());

        return orderItem;
    }

    public ProductDimensions mapProductDimensions(ProductDimensionsDTO dimensionsDTO) {
        if (dimensionsDTO == null) return null;

        ProductDimensions dimensions = new ProductDimensions();
        dimensions.setLength(dimensionsDTO.getLength());
        dimensions.setWidth(dimensionsDTO.getWidth());
        dimensions.setUnit(dimensionsDTO.getUnit());
        return dimensions;
    }

    public BigDecimal calculateTotalAmount(Order order) {
        return order.getItem().getSubtotal();
    }

    public OrderResponseDTO mapToOrderResponse(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setUserId(order.getUserId());
        responseDTO.setItem(mapToOrderItemResponse(order.getItem()));
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setPaymentStatus(order.getPaymentStatus());
        responseDTO.setShippingAddress(order.getShippingAddress());
        responseDTO.setSpecialInstructions(order.getSpecialInstructions());
        responseDTO.setCreatedAt(order.getCreatedAt());
        responseDTO.setUpdatedAt(order.getUpdatedAt());
        return responseDTO;
    }

    public OrderItemResponseDTO mapToOrderItemResponse(OrderItem item) {
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