package com.edureka.microservices.aggregation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable Long productId);

}
