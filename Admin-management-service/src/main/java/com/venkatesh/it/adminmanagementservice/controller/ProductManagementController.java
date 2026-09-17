package com.venkatesh.it.adminmanagementservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products (Admin and Super Admin)")
@SecurityRequirement(name = "bearerAuth")
public class ProductManagementController {

    private final RestTemplate restTemplate;

    @Value("${products.service.base-url:http://localhost:8082}")
    private String productsServiceBaseUrl;

    private String getProductsServiceUrl(String path) {
        return productsServiceBaseUrl + "/api/products" + path;
    }

    private HttpHeaders createHeadersWithForwardedAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @GetMapping("/admin/all")
    @Operation(summary = "Get all products for admin", description = "Retrieve all products for admin dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        String url = getProductsServiceUrl("/admin/all") + "?page=" + page + "&size=" + size + "&sort=" + sort;
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping
    @Operation(summary = "Get products with filters", description = "Retrieve products with optional filters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        StringBuilder url = new StringBuilder(getProductsServiceUrl(""));
        url.append("?categoryId=").append(categoryId != null ? categoryId : "")
           .append("&search=").append(search != null ? search : "")
           .append("&status=").append(status != null ? status : "")
           .append("&page=").append(page)
           .append("&size=").append(size)
           .append("&sort=").append(sort);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        String url = getProductsServiceUrl("/" + id);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> productData) {
        String url = getProductsServiceUrl("");
        log.debug("Forwarding POST request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(productData, createHeadersWithForwardedAuth()), Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> productData) {
        String url = getProductsServiceUrl("/" + id);
        log.debug("Forwarding PUT request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(productData, createHeadersWithForwardedAuth()), Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        String url = getProductsServiceUrl("/" + id);
        log.debug("Forwarding DELETE request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update product status", description = "Update product status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProductStatus(@PathVariable Long id, @RequestParam String status) {
        String url = getProductsServiceUrl("/" + id + "/status?status=" + status);
        log.debug("Forwarding PATCH request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Update product stock quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        String url = getProductsServiceUrl("/" + id + "/stock?quantity=" + quantity);
        log.debug("Forwarding PUT request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }
}
