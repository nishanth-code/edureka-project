package com.edureka.microservices.aggregation.client;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category
) {}
