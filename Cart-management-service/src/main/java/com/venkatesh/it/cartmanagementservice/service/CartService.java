package com.venkatesh.it.cartmanagementservice.service;

import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartItemResponse;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;

import java.util.List;

public interface CartService {
    CartResponse getCartByUserId(Long userId);
    CartResponse addItemToCart(Long userId, CartItemRequest request);
    CartResponse updateCartItem(Long userId, Long productId, Integer quantity);
    CartResponse removeItemFromCart(Long userId, Long productId);
    CartResponse clearCart(Long userId);
    List<CartResponse> getAllCarts();
}
