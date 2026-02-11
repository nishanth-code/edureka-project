package com.edureka.microservices.inventory.controller;

import com.edureka.microservices.inventory.dto.InventoryResponse;
import com.edureka.microservices.inventory.dto.StockUpdateRequest;
import com.edureka.microservices.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Inventory and stock management endpoints")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add")
    @Operation(summary = "Add stock for a product")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody StockUpdateRequest request) {
        logger.info("Adding stock for product: {}", request.productId());
        InventoryResponse response = inventoryService.addStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    @Operation(summary = "Update stock for a product")
    public ResponseEntity<InventoryResponse> updateStock(@Valid @RequestBody StockUpdateRequest request) {
        logger.info("Updating stock for product: {}", request.productId());
        InventoryResponse response = inventoryService.updateStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory for a product")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        logger.info("Fetching inventory for product: {}", productId);
        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-availability")
    @Operation(summary = "Check stock availability")
    public ResponseEntity<InventoryResponse> checkAvailability(
            @RequestParam Long productId,
            @RequestParam Integer requiredQuantity) {
        logger.info("Checking availability for product: {} with quantity: {}", productId, requiredQuantity);
        InventoryResponse response = inventoryService.checkAvailability(productId, requiredQuantity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decrease")
    @Operation(summary = "Decrease stock for a product")
    public ResponseEntity<InventoryResponse> decreaseStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        logger.info("Decreasing stock for product: {} by quantity: {}", productId, quantity);
        InventoryResponse response = inventoryService.decreaseStock(productId, quantity);
        return ResponseEntity.ok(response);
    }

}
