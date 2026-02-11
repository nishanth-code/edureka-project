package com.edureka.microservices.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    Integer quantity
) {}
