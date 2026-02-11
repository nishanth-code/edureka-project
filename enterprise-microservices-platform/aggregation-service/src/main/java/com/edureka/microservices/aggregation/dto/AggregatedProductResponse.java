package com.edureka.microservices.aggregation.dto;

import java.math.BigDecimal;

public record AggregatedProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    Boolean stockAvailable,
    Integer quantity
) {}
