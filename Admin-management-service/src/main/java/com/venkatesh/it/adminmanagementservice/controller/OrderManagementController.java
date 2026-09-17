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
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders (Admin and Super Admin)")
@SecurityRequirement(name = "bearerAuth")
public class OrderManagementController {

    private final RestTemplate restTemplate;

    @Value("${order.service.base-url:http://localhost:8084}")
    private String orderServiceBaseUrl;

    private String getOrderServiceUrl(String path) {
        return orderServiceBaseUrl + "/api/v1/orders" + path;
    }

    private HttpHeaders createHeadersWithForwardedAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders for admin dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        String url = getOrderServiceUrl("");
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        String url = getOrderServiceUrl("/" + orderId);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieve a specific order by order number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        String url = getOrderServiceUrl("/number/" + orderNumber);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID", description = "Retrieve all orders for a specific user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        String url = getOrderServiceUrl("/user/" + userId);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer ID", description = "Retrieve all orders for a specific customer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable Long customerId) {
        String url = getOrderServiceUrl("/customer/" + customerId);
        log.debug("Forwarding request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order", description = "Update an existing order")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> orderData) {
        String url = getOrderServiceUrl("/" + orderId);
        log.debug("Forwarding PUT request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(orderData, createHeadersWithForwardedAuth()), Object.class);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        String url = getOrderServiceUrl("/" + orderId + "/cancel");
        log.debug("Forwarding POST request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order", description = "Delete an order (Super Admin only)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        String url = getOrderServiceUrl("/" + orderId);
        log.debug("Forwarding DELETE request to: {}", url);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeadersWithForwardedAuth()), Object.class);
    }
}
