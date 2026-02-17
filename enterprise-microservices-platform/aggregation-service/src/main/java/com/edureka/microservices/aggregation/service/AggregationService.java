package com.edureka.microservices.aggregation.service;

import com.edureka.microservices.aggregation.client.InventoryResponse;
import com.edureka.microservices.aggregation.client.InventoryServiceClient;
import com.edureka.microservices.aggregation.client.ProductResponse;
import com.edureka.microservices.aggregation.client.ProductServiceClient;
import com.edureka.microservices.aggregation.dto.AggregatedProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class AggregationService {

    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

    private final ObjectProvider<ProductServiceClient> productServiceClientProvider;
    private final ObjectProvider<InventoryServiceClient> inventoryServiceClientProvider;

    public AggregationService(ObjectProvider<ProductServiceClient> productServiceClientProvider, ObjectProvider<InventoryServiceClient> inventoryServiceClientProvider) {
        this.productServiceClientProvider = productServiceClientProvider;
        this.inventoryServiceClientProvider = inventoryServiceClientProvider;
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getAggregatedProductFallback")
    public AggregatedProductResponse getAggregatedProduct(Long productId) {
        logger.info("Aggregating data for product: {}", productId);

        ProductResponse product = productServiceClientProvider.getObject().getProductById(productId);
        logger.info("Retrieved product from product-service: {}", productId);

        InventoryResponse inventory;
        try {
            inventory = inventoryServiceClientProvider.getObject().getInventoryByProductId(productId);
            logger.info("Retrieved inventory from inventory-service: {}", productId);
        } catch (Exception ex) {
            logger.warn("Failed to retrieve inventory for product: {}, using default", productId);
            inventory = new InventoryResponse(null, productId, 0, false);
        }

        return new AggregatedProductResponse(
            product.id(),
            product.name(),
            product.description(),
            product.price(),
            product.category(),
            inventory.available(),
            inventory.quantity()
        );
    }

    public AggregatedProductResponse getAggregatedProductFallback(Long productId, Exception ex) {
        logger.error("Circuit breaker activated for product service, productId: {}", productId);
        throw new RuntimeException("Product service is unavailable", ex);
    }

}
