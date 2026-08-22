package com.ashok.it.adminservice.service;

import com.ashok.it.adminservice.dto.ProductCategoryRequest;
import com.ashok.it.adminservice.dto.ProductCategoryResponse;
import com.ashok.it.adminservice.entity.ProductCategory;
import com.ashok.it.adminservice.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ProductCategoryService {
    
    private final ProductCategoryRepository productCategoryRepository;
    
    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }
    
    public Mono<ProductCategoryResponse> createCategory(ProductCategoryRequest request, String createdBy) {
        return productCategoryRepository.findByName(request.getName())
                .flatMap(existing -> Mono.<ProductCategoryResponse>error(
                        new RuntimeException("Category with name '" + request.getName() + "' already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    ProductCategory category = new ProductCategory();
                    category.setName(request.getName());
                    category.setDescription(request.getDescription());
                    category.setActive(request.getActive() != null ? request.getActive() : true);
                    category.setCreatedAt(LocalDateTime.now());
                    category.setUpdatedAt(LocalDateTime.now());
                    category.setCreatedBy(createdBy);
                    category.setUpdatedBy(createdBy);
                    return productCategoryRepository.save(category)
                            .map(this::mapToResponse);
                }));
    }
    
    public Mono<ProductCategoryResponse> getCategoryById(Long id) {
        return productCategoryRepository.findById(id)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + id)));
    }
    
    public Flux<ProductCategoryResponse> getAllCategories() {
        return productCategoryRepository.findAll()
                .map(this::mapToResponse);
    }
    
    public Flux<ProductCategoryResponse> getActiveCategories() {
        return productCategoryRepository.findByActive(true)
                .map(this::mapToResponse);
    }
    
    public Mono<ProductCategoryResponse> updateCategory(Long id, ProductCategoryRequest request, String updatedBy) {
        return productCategoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + id)))
                .flatMap(category -> {
                    category.setName(request.getName());
                    category.setDescription(request.getDescription());
                    if (request.getActive() != null) {
                        category.setActive(request.getActive());
                    }
                    category.setUpdatedAt(LocalDateTime.now());
                    category.setUpdatedBy(updatedBy);
                    return productCategoryRepository.save(category)
                            .map(this::mapToResponse);
                });
    }
    
    public Mono<Void> deleteCategory(Long id) {
        return productCategoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + id)))
                .flatMap(category -> productCategoryRepository.deleteById(id));
    }
    
    private ProductCategoryResponse mapToResponse(ProductCategory category) {
        ProductCategoryResponse response = new ProductCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.getActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setCreatedBy(category.getCreatedBy());
        response.setUpdatedBy(category.getUpdatedBy());
        return response;
    }
}
