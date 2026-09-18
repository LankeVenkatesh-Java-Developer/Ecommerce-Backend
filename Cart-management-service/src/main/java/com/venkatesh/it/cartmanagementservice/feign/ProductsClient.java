package com.venkatesh.it.cartmanagementservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "products-service", url = "${products.service.url:http://localhost:8082}")
public interface ProductsClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    record ProductDTO(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity,
            String status,
            String imageUrl,
            String sku,
            String brand,
            CategoryDTO category
    ) {}

    record CategoryDTO(
            Long id,
            String name,
            String description,
            String imageUrl
    ) {}
}
