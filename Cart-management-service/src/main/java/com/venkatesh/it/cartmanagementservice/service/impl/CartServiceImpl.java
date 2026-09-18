package com.venkatesh.it.cartmanagementservice.service.impl;

import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartItemResponse;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.entity.Cart;
import com.venkatesh.it.cartmanagementservice.entity.CartItem;
import com.venkatesh.it.cartmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.cartmanagementservice.feign.ProductsClient;
import com.venkatesh.it.cartmanagementservice.repository.CartItemRepository;
import com.venkatesh.it.cartmanagementservice.repository.CartRepository;
import com.venkatesh.it.cartmanagementservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductsClient productsClient;

    @Override
    public CartResponse getCartByUserId(Long userId) {
        log.info("Fetching cart for user: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());

        // Validate product and get details from Product Service
        try {
            ProductsClient.ProductDTO product = productsClient.getProductById(request.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found: " + request.getProductId());
            }
            // Validate product is active
            if (!"ACTIVE".equalsIgnoreCase(product.status())) {
                throw new IllegalStateException("Product is not available: " + product.name());
            }
            // Validate stock
            if (product.quantity() < request.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.name() +
                        ". Available: " + product.quantity() + ", Requested: " + request.getQuantity());
            }

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));

            // Check if item already exists
            CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                    .orElse(null);

            if (existingItem != null) {
                // Update quantity
                int newQuantity = existingItem.getQuantity() + request.getQuantity();
                if (newQuantity > 100) {
                    throw new IllegalArgumentException("Total quantity cannot exceed 100");
                }
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem);
                log.info("Updated quantity for existing item: {}", request.getProductId());
            } else {
                // Add new item with data from product service
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .productId(request.getProductId())
                        .productName(product.name())
                        .productImageUrl(product.imageUrl())
                        .productSku(product.sku())
                        .productBrand(product.brand())
                        .quantity(request.getQuantity())
                        .price(product.price())
                        .build();
                cartItemRepository.save(newItem);
                cart.addItem(newItem);
                cartRepository.save(cart);
                log.info("Added new item to cart: {}", request.getProductId());
            }

            cart.updateTotals();
            Cart savedCart = cartRepository.save(cart);
            return mapToCartResponse(savedCart);
        } catch (Exception e) {
            log.error("Failed to add item to cart", e);
            throw new IllegalStateException("Unable to add item to cart. Please try again later.");
        }
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long productId, Integer quantity) {
        log.info("Updating cart item for user: {}, product: {}, quantity: {}", userId, productId, quantity);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart: " + productId));

        if (quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cart.updateTotals();
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        log.info("Removing item from cart for user: {}, product: {}", userId, productId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart: " + productId));

        cart.removeItem(item);
        cartItemRepository.delete(item);
        cart.updateTotals();
        
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    public CartResponse clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cart.getItems().clear();
        cart.updateTotals();
        
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        log.info("Fetching all carts");
        return cartRepository.findAll().stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .totalAmount(java.math.BigDecimal.ZERO)
                .totalItems(0)
                .build();
        return cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .total(cart.getTotalAmount() != null ? cart.getTotalAmount().doubleValue() : 0.0)
                .totalItems(cart.getTotalItems())
                .items(itemResponses)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        // Get current stock from product service
        Integer stockQuantity = null;
        try {
            ProductsClient.ProductDTO product = productsClient.getProductById(item.getProductId());
            if (product != null) {
                stockQuantity = product.quantity();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch stock for product: {}", item.getProductId(), e);
        }

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .name(item.getProductName())
                .imageUrl(item.getProductImageUrl())
                .sku(item.getProductSku())
                .brand(item.getProductBrand())
                .quantity(item.getQuantity())
                .stockQuantity(stockQuantity)
                .price(item.getPrice() != null ? item.getPrice().doubleValue() : 0.0)
                .total(item.getTotal() != null ? item.getTotal().doubleValue() : 0.0)
                .build();
    }
}
