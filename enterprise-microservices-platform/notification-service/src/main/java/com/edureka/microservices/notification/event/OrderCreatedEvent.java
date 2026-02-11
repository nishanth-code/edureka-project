package com.edureka.microservices.notification.event;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
    Long orderId,
    Long userId,
    Long productId,
    Integer quantity,
    String status,
    LocalDateTime createdAt
) {}
