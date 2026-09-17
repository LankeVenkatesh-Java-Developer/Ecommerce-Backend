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
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing categories (Admin and Super Admin)")
@SecurityRequirement(name = "bearerAuth")
public class CategoryManagementController {

    private final RestTemplate restTemplate;

    @Value("${products.service.base-url:http://localhost:8082}")
    private String productsServiceBaseUrl;

    private String getCategoriesServiceUrl(String path) {
        return productsServiceBaseUrl + "/api/categories" + path;
    }

    private HttpHeaders createHeadersWithForwardedAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @GetMapping("/admin/all")
    @Operation(summary = "Get all categories for admin", description = "Retrieve all categories for admin dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllCategoriesForAdmin() {
        String url = getCategoriesServiceUrl("/admin/all");
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllCategories() {
        String url = getCategoriesServiceUrl("");
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        String url = getCategoriesServiceUrl("/" + id);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> categoryData) {
        String url = getCategoriesServiceUrl("");
        log.debug("Forwarding POST request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(categoryData, createHeadersWithForwardedAuth()), Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object> categoryData) {
        String url = getCategoriesServiceUrl("/" + id);
        log.debug("Forwarding PUT request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(categoryData, createHeadersWithForwardedAuth()), Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        String url = getCategoriesServiceUrl("/" + id);
        log.debug("Forwarding DELETE request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }
}
