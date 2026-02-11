package com.edureka.microservices.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category
) {}
