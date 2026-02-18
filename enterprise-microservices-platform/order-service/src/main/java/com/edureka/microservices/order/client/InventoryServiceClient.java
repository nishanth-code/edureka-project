package com.edureka.microservices.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "inventory-service",
        url = "http://localhost:8084"
)
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/decrease")
    String decreaseStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );

    @PostMapping("/api/inventory/check-availability")
    String checkAvailability(
            @RequestParam("productId") Long productId,
            @RequestParam("requiredQuantity") Integer requiredQuantity
    );
}
