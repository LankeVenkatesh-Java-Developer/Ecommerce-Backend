package com.ashok.it.adminservice.controller;

import com.ashok.it.adminservice.dto.ProductCategoryRequest;
import com.ashok.it.adminservice.dto.ProductCategoryResponse;
import com.ashok.it.adminservice.service.ProductCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    
    private final ProductCategoryService productCategoryService;
    
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
    
    @PostMapping
    public Mono<ResponseEntity<ProductCategoryResponse>> createCategory(
            @Valid @RequestBody ProductCategoryRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return productCategoryService.createCategory(request, userId)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductCategoryResponse>> getCategoryById(@PathVariable Long id) {
        return productCategoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
    
    @GetMapping
    public Flux<ProductCategoryResponse> getAllCategories() {
        return productCategoryService.getAllCategories();
    }
    
    @GetMapping("/active")
    public Flux<ProductCategoryResponse> getActiveCategories() {
        return productCategoryService.getActiveCategories();
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ProductCategoryRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return productCategoryService.updateCategory(id, request, userId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCategory(@PathVariable Long id) {
        return productCategoryService.deleteCategory(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build()));
    }
}
