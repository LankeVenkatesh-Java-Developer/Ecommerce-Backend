package com.ashok.it.adminservice.repository;

import com.ashok.it.adminservice.entity.ProductCategory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, Long> {
    
    Flux<ProductCategory> findByActive(Boolean active);
    
    Mono<ProductCategory> findByName(String name);
    
    Flux<ProductCategory> findByCreatedBy(String createdBy);
}
