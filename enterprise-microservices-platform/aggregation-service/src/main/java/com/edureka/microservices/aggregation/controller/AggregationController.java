package com.edureka.microservices.aggregation.controller;

import com.edureka.microservices.aggregation.dto.AggregatedProductResponse;
import com.edureka.microservices.aggregation.service.AggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aggregate")
@Tag(name = "Aggregation", description = "BFF endpoints that aggregate data from multiple services")
public class AggregationController {

    private static final Logger logger = LoggerFactory.getLogger(AggregationController.class);

    private final AggregationService aggregationService;

    public AggregationController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get aggregated product data with stock availability")
    public ResponseEntity<AggregatedProductResponse> getAggregatedProduct(@PathVariable Long productId) {
        logger.info("Aggregating product data for: {}", productId);
        AggregatedProductResponse response = aggregationService.getAggregatedProduct(productId);
        return ResponseEntity.ok(response);
    }

}
