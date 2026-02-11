package com.edureka.microservices.order.event;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
    Long orderId,
    Long userId,
    Long productId,
    Integer quantity,
    String status,
    LocalDateTime createdAt
) {}
