package com.venkatesh.it.ordermanagementservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "products-service", url = "${products.service.url:http://localhost:8082}")
public interface ProductsClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}/stock")
    ProductDTO updateProductStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity
    );

    record ProductDTO(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity,
            String status,
            String imageUrl
    ) {}
}
