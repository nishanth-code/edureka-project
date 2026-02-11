package com.edureka.microservices.inventory.service;

import com.edureka.microservices.inventory.dto.InventoryResponse;
import com.edureka.microservices.inventory.dto.StockUpdateRequest;
import com.edureka.microservices.inventory.entity.Inventory;
import com.edureka.microservices.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse addStock(StockUpdateRequest request) {
        logger.info("Adding stock for product: {}, quantity: {}", request.productId(), request.quantity());

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
            .orElseGet(() -> {
                Inventory newInventory = new Inventory(request.productId(), 0);
                return inventoryRepository.save(newInventory);
            });

        inventory.setQuantity(inventory.getQuantity() + request.quantity());
        Inventory updated = inventoryRepository.save(inventory);
        logger.info("Stock added successfully for product: {}", request.productId());
        return toResponse(updated);
    }

    public InventoryResponse updateStock(StockUpdateRequest request) {
        logger.info("Updating stock for product: {}, quantity: {}", request.productId(), request.quantity());

        Inventory inventory = inventoryRepository.findByProductId(request.productId())
            .orElseThrow(() -> {
                logger.error("Inventory not found for product: {}", request.productId());
                return new IllegalArgumentException("Inventory not found for product: " + request.productId());
            });

        inventory.setQuantity(request.quantity());
        Inventory updated = inventoryRepository.save(inventory);
        logger.info("Stock updated successfully for product: {}", request.productId());
        return toResponse(updated);
    }

    public InventoryResponse checkAvailability(Long productId, Integer requiredQuantity) {
        logger.info("Checking availability for product: {}, required quantity: {}", productId, requiredQuantity);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> {
                logger.error("Inventory not found for product: {}", productId);
                return new IllegalArgumentException("Inventory not found for product: " + productId);
            });

        return toResponse(inventory);
    }

    public InventoryResponse getInventoryByProductId(Long productId) {
        logger.info("Fetching inventory for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> {
                logger.error("Inventory not found for product: {}", productId);
                return new IllegalArgumentException("Inventory not found for product: " + productId);
            });

        return toResponse(inventory);
    }

    public InventoryResponse decreaseStock(Long productId, Integer quantity) {
        logger.info("Decreasing stock for product: {}, quantity: {}", productId, quantity);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> {
                logger.error("Inventory not found for product: {}", productId);
                return new IllegalArgumentException("Inventory not found for product: " + productId);
            });

        if (inventory.getQuantity() < quantity) {
            logger.warn("Insufficient stock for product: {}", productId);
            throw new IllegalArgumentException("Insufficient stock for product: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        Inventory updated = inventoryRepository.save(inventory);
        logger.info("Stock decreased successfully for product: {}", productId);
        return toResponse(updated);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
            inventory.getId(),
            inventory.getProductId(),
            inventory.getQuantity(),
            inventory.getQuantity() > 0
        );
    }

}
