package com.ashok.it.adminservice.repository;

import com.ashok.it.adminservice.entity.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    
    Flux<Product> findByCategoryId(Long categoryId);
    
    Flux<Product> findByActive(Boolean active);
    
    Mono<Product> findBySku(String sku);
    
    Flux<Product> findByCreatedBy(String createdBy);
    
    Flux<Product> findByNameContainingIgnoreCase(String name);
}
