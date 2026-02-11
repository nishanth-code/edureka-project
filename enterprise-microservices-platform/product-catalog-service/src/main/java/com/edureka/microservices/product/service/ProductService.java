package com.edureka.microservices.product.service;

import com.edureka.microservices.product.dto.CreateProductRequest;
import com.edureka.microservices.product.dto.ProductResponse;
import com.edureka.microservices.product.entity.Product;
import com.edureka.microservices.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        logger.info("Creating product: {}", request.name());
        Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.category()
        );
        Product saved = productRepository.save(product);
        logger.info("Product created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    public ProductResponse getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        return toResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        logger.info("Fetching products for category: {}", category);
        return productRepository.findByCategory(category)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProductsByName(String name) {
        logger.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id, CreateProductRequest request) {
        logger.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());

        Product updated = productRepository.save(product);
        logger.info("Product updated: {}", id);
        return toResponse(updated);
    }

    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        logger.info("Product deleted: {}", id);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory()
        );
    }

}
