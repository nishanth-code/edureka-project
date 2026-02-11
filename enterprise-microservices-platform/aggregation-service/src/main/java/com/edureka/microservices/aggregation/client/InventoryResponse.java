package com.edureka.microservices.aggregation.client;

public record InventoryResponse(
    Long id,
    Long productId,
    Integer quantity,
    Boolean available
) {}
