package com.artztall.order_service.controller;


import com.artztall.orderservice.dto.OrderCreateDTO;
import com.artztall.orderservice.dto.OrderResponseDTO;
import com.artztall.orderservice.model.OrderStatus;
import com.artztall.orderservice.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Api(tags = "Order Management APIs")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ApiOperation("Create a new order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderCreateDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @ApiOperation("Get order by ID")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/user/{userId}")
    @ApiOperation("Get all orders for a user")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @PutMapping("/{orderId}/status")
    @ApiOperation("Update order status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/{orderId}")
    @ApiOperation("Delete an order")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}