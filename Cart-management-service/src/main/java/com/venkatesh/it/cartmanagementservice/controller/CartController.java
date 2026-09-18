package com.venkatesh.it.cartmanagementservice.controller;

import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCurrentUserCart(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId, Authentication authentication) {
        // Use authenticated user ID instead of path parameter for security
        Long authenticatedUserId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.getCartByUserId(authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItemToCartByUserId(
            @PathVariable Long userId,
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        // Use authenticated user ID instead of path parameter for security
        Long authenticatedUserId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.addItemToCart(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            Authentication authentication,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItemByUserId(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        // Use authenticated user ID instead of path parameter for security
        Long authenticatedUserId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.updateCartItem(authenticatedUserId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            Authentication authentication,
            @PathVariable Long productId) {
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCartByUserId(
            @PathVariable Long userId,
            @PathVariable Long productId,
            Authentication authentication) {
        // Use authenticated user ID instead of path parameter for security
        Long authenticatedUserId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.removeItemFromCart(authenticatedUserId, productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.clearCart(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartResponse> clearCartByUserId(
            @PathVariable Long userId,
            Authentication authentication) {
        // Use authenticated user ID instead of path parameter for security
        Long authenticatedUserId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.clearCart(authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        List<CartResponse> response = cartService.getAllCarts();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/clear/{userId}")
    public ResponseEntity<CartResponse> clearCartByUserId(@PathVariable Long userId) {
        CartResponse response = cartService.clearCart(userId);
        return ResponseEntity.ok(response);
    }
}
