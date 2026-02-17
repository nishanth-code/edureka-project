package com.edureka.microservices.order.service;

import com.edureka.microservices.order.client.InventoryServiceClient;
import com.edureka.microservices.order.dto.CreateOrderRequest;
import com.edureka.microservices.order.dto.OrderResponse;
import com.edureka.microservices.order.entity.Order;
import com.edureka.microservices.order.entity.OrderStatus;
import com.edureka.microservices.order.event.OrderCreatedEvent;
import com.edureka.microservices.order.kafka.OrderEventProducer;
import com.edureka.microservices.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ObjectProvider<InventoryServiceClient> inventoryServiceClientProvider;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository, ObjectProvider<InventoryServiceClient> inventoryServiceClientProvider, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClientProvider = inventoryServiceClientProvider;
        this.orderEventProducer = orderEventProducer;
    }

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Creating order for user: {}, product: {}, quantity: {}", request.userId(), request.productId(), request.quantity());

        try {
            inventoryServiceClientProvider.getObject().checkAvailability(request.productId(), request.quantity());
            logger.info("Stock availability confirmed for product: {}", request.productId());
        } catch (Exception ex) {
            logger.error("Failed to check inventory availability: {}", ex.getMessage());
            throw new RuntimeException("Inventory service unavailable", ex);
        }

        Order order = new Order(request.userId(), request.productId(), request.quantity(), OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with ID: {}", savedOrder.getId());

        try {
            inventoryServiceClientProvider.getObject().decreaseStock(request.productId(), request.quantity());
            logger.info("Stock decreased for product: {}", request.productId());

            order.setStatus(OrderStatus.CONFIRMED);
            Order updatedOrder = orderRepository.save(order);

            OrderCreatedEvent event = new OrderCreatedEvent(
                updatedOrder.getId(),
                updatedOrder.getUserId(),
                updatedOrder.getProductId(),
                updatedOrder.getQuantity(),
                updatedOrder.getStatus().name(),
                updatedOrder.getCreatedAt()
            );
            orderEventProducer.publishOrderCreatedEvent(event);
            logger.info("Order confirmed and event published for order: {}", updatedOrder.getId());

            return toResponse(updatedOrder);
        } catch (Exception ex) {
            logger.error("Failed to decrease stock for product: {}, error: {}", request.productId(), ex.getMessage());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new RuntimeException("Failed to process order", ex);
        }
    }

    public OrderResponse createOrderFallback(CreateOrderRequest request, Exception ex) {
        logger.warn("Circuit breaker activated for inventory service, creating order in PENDING state");
        Order order = new Order(request.userId(), request.productId(), request.quantity(), OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
            savedOrder.getId(),
            savedOrder.getUserId(),
            savedOrder.getProductId(),
            savedOrder.getQuantity(),
            savedOrder.getStatus().name(),
            savedOrder.getCreatedAt()
        );
        orderEventProducer.publishOrderCreatedEvent(event);

        return toResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        logger.info("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        return toResponse(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        logger.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<OrderResponse> getProductOrders(Long productId) {
        logger.info("Fetching orders for product: {}", productId);
        return orderRepository.findByProductId(productId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUserId(),
            order.getProductId(),
            order.getQuantity(),
            order.getStatus().name()
        );
    }

}
