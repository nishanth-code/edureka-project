package com.edureka.microservices.order.dto;

public record OrderResponse(
    Long id,
    Long userId,
    Long productId,
    Integer quantity,
    String status
) {}
