package com.edureka.microservices.inventory.dto;

public record InventoryResponse(
    Long id,
    Long productId,
    Integer quantity,
    Boolean available
) {}
