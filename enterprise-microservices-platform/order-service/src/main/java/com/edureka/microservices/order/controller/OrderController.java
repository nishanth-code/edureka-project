package com.edureka.microservices.order.controller;

import com.edureka.microservices.order.dto.CreateOrderRequest;
import com.edureka.microservices.order.dto.OrderResponse;
import com.edureka.microservices.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Creating order for user: {}", request.userId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") Long id) {
        logger.info("Fetching order: {}", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable("userId") Long userId) {
        logger.info("Fetching orders for user: {}", userId);
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get all orders for a product")
    public ResponseEntity<List<OrderResponse>> getProductOrders(@PathVariable Long productId) {
        logger.info("Fetching orders for product: {}", productId);
        List<OrderResponse> orders = orderService.getProductOrders(productId);
        return ResponseEntity.ok(orders);
    }

}
