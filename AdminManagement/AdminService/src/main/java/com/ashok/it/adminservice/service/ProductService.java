package com.ashok.it.adminservice.service;

import com.ashok.it.adminservice.dto.ProductRequest;
import com.ashok.it.adminservice.dto.ProductResponse;
import com.ashok.it.adminservice.entity.Product;
import com.ashok.it.adminservice.entity.ProductCategory;
import com.ashok.it.adminservice.repository.ProductCategoryRepository;
import com.ashok.it.adminservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }
    
    public Mono<ProductResponse> createProduct(ProductRequest request, String createdBy) {
        return productCategoryRepository.findById(request.getCategoryId())
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + request.getCategoryId())))
                .flatMap(category -> productRepository.findBySku(request.getSku())
                        .flatMap(existing -> Mono.<ProductResponse>error(
                                new RuntimeException("Product with SKU '" + request.getSku() + "' already exists")))
                        .switchIfEmpty(Mono.defer(() -> {
                            Product product = new Product();
                            product.setName(request.getName());
                            product.setDescription(request.getDescription());
                            product.setSku(request.getSku());
                            product.setPrice(request.getPrice());
                            product.setQuantity(request.getQuantity());
                            product.setCategoryId(request.getCategoryId());
                            product.setImageUrl(request.getImageUrl());
                            product.setActive(request.getActive() != null ? request.getActive() : true);
                            product.setCreatedAt(LocalDateTime.now());
                            product.setUpdatedAt(LocalDateTime.now());
                            product.setCreatedBy(createdBy);
                            product.setUpdatedBy(createdBy);
                            return productRepository.save(product)
                                    .map(savedProduct -> mapToResponse(savedProduct, category));
                        })));
    }
    
    public Mono<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .flatMap(product -> productCategoryRepository.findById(product.getCategoryId())
                        .map(category -> mapToResponse(product, category)))
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + id)));
    }
    
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .flatMap(product -> productCategoryRepository.findById(product.getCategoryId())
                        .map(category -> mapToResponse(product, category)));
    }
    
    public Flux<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .flatMap(product -> productCategoryRepository.findById(product.getCategoryId())
                        .map(category -> mapToResponse(product, category)));
    }
    
    public Flux<ProductResponse> getActiveProducts() {
        return productRepository.findByActive(true)
                .flatMap(product -> productCategoryRepository.findById(product.getCategoryId())
                        .map(category -> mapToResponse(product, category)));
    }
    
    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request, String updatedBy) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + id)))
                .flatMap(product -> {
                    if (!product.getCategoryId().equals(request.getCategoryId())) {
                        return productCategoryRepository.findById(request.getCategoryId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + request.getCategoryId())))
                                .flatMap(category -> updateProductFields(product, request, updatedBy)
                                        .map(saved -> mapToResponse(saved, category)));
                    }
                    return productCategoryRepository.findById(product.getCategoryId())
                            .flatMap(category -> updateProductFields(product, request, updatedBy)
                                    .map(saved -> mapToResponse(saved, category)));
                });
    }
    
    private Mono<Product> updateProductFields(Product product, ProductRequest request, String updatedBy) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(updatedBy);
        return productRepository.save(product);
    }
    
    public Mono<Void> deleteProduct(Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found with id: " + id)))
                .flatMap(product -> productRepository.deleteById(id));
    }
    
    public Flux<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .flatMap(product -> productCategoryRepository.findById(product.getCategoryId())
                        .map(category -> mapToResponse(product, category)));
    }
    
    private ProductResponse mapToResponse(Product product, ProductCategory category) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setCategoryId(product.getCategoryId());
        response.setCategoryName(category != null ? category.getName() : null);
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setCreatedBy(product.getCreatedBy());
        response.setUpdatedBy(product.getUpdatedBy());
        return response;
    }
}
