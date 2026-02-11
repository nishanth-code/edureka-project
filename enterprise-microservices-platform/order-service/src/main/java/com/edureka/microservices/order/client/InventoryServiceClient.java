package com.edureka.microservices.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/decrease")
    String decreaseStock(@RequestParam Long productId, @RequestParam Integer quantity);

    @PostMapping("/api/inventory/check-availability")
    String checkAvailability(@RequestParam Long productId, @RequestParam Integer requiredQuantity);

}
