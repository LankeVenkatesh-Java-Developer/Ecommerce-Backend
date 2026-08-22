package com.ashok.it.adminservice.controller;

import com.ashok.it.adminservice.dto.ProductRequest;
import com.ashok.it.adminservice.dto.ProductResponse;
import com.ashok.it.adminservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return productService.createProduct(request, userId)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
    
    @GetMapping
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/category/{categoryId}")
    public Flux<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
    
    @GetMapping("/active")
    public Flux<ProductResponse> getActiveProducts() {
        return productService.getActiveProducts();
    }
    
    @GetMapping("/search")
    public Flux<ProductResponse> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return productService.updateProduct(id, request, userId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build()));
    }
}
