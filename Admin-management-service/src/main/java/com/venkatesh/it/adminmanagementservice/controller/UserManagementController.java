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
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users (Super Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final RestTemplate restTemplate;

    @Value("${user.service.base-url:http://localhost:8081/api/v1}")
    private String userServiceBaseUrl;

    private String getUserServiceUrl(String path) {
        return userServiceBaseUrl + path;
    }

    private HttpHeaders createHeadersWithForwardedAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        String url = getUserServiceUrl("/users");
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        String url = getUserServiceUrl("/users/" + id);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        String url = getUserServiceUrl("/users/" + id);
        log.debug("Forwarding PUT request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(userData, createHeadersWithForwardedAuth()), Object.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        String url = getUserServiceUrl("/users/" + id);
        log.debug("Forwarding DELETE request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Update user role (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> roleData) {
        String url = getUserServiceUrl("/users/" + id + "/role");
        log.debug("Forwarding PATCH request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(roleData, createHeadersWithForwardedAuth()), Object.class);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "Update user status (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        String url = getUserServiceUrl("/users/" + id + "/status");
        log.debug("Forwarding PATCH request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(statusData, createHeadersWithForwardedAuth()), Object.class);
    }
}
