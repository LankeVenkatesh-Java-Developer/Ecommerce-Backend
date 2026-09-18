package com.venkatesh.it.cartmanagementservice.service;

import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.entity.Cart;
import com.venkatesh.it.cartmanagementservice.entity.CartItem;
import com.venkatesh.it.cartmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.cartmanagementservice.feign.ProductsClient;
import com.venkatesh.it.cartmanagementservice.repository.CartItemRepository;
import com.venkatesh.it.cartmanagementservice.repository.CartRepository;
import com.venkatesh.it.cartmanagementservice.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductsClient productsClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private CartItem testCartItem;
    private CartItemRequest cartItemRequest;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .id(1L)
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();

        testCartItem = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .total(new BigDecimal("199.98"))
                .build();

        cartItemRequest = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();
    }

    @Test
    void whenGetCartByUserIdExists_thenReturnCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        CartResponse response = cartService.getCartByUserId(1L);

        assertNotNull(response);
        assertEquals(testCart.getUserId(), response.getUserId());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void whenGetCartByUserIdNotExists_thenCreateNewCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.getCartByUserId(1L);

        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void whenAddItemToCart_thenItemAdded() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 2, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.addItemToCart(1L, cartItemRequest);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, atLeast(1)).save(any(Cart.class));
    }

    @Test
    void whenAddItemToCartProductNotActive_thenThrowException() {
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 2, "INACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        assertThrows(IllegalStateException.class, () -> {
            cartService.addItemToCart(1L, cartItemRequest);
        });
    }

    @Test
    void whenAddItemToCartInsufficientStock_thenThrowException() {
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 1, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        assertThrows(IllegalStateException.class, () -> {
            cartService.addItemToCart(1L, cartItemRequest);
        });
    }

    @Test
    void whenAddItemToCartProductNotFound_thenThrowException() {
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productsClient.getProductById(1L)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> {
            cartService.addItemToCart(1L, cartItemRequest);
        });
    }

    @Test
    void whenAddItemToCartExistingItem_thenUpdateQuantity() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.addItemToCart(1L, cartItemRequest);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void whenAddItemToCartQuantityExceedsLimit_thenThrowException() {
        testCartItem.setQuantity(99);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        assertThrows(IllegalStateException.class, () -> {
            cartService.addItemToCart(1L, cartItemRequest);
        });
    }

    @Test
    void whenUpdateCartItem_thenItemUpdated() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.updateCartItem(1L, 1L, 5);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void whenUpdateCartItemQuantityZero_thenItemRemoved() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.updateCartItem(1L, 1L, 0);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
    }

    @Test
    void whenUpdateCartItemCartNotFound_thenThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateCartItem(1L, 1L, 5);
        });
    }

    @Test
    void whenUpdateCartItemNotFound_thenThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateCartItem(1L, 1L, 5);
        });
    }

    @Test
    void whenRemoveItemFromCart_thenItemRemoved() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.removeItemFromCart(1L, 1L);

        assertNotNull(response);
        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
    }

    @Test
    void whenRemoveItemFromCartCartNotFound_thenThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeItemFromCart(1L, 1L);
        });
    }

    @Test
    void whenRemoveItemFromCartItemNotFound_thenThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeItemFromCart(1L, 1L);
        });
    }

    @Test
    void whenClearCart_thenCartCleared() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        CartResponse response = cartService.clearCart(1L);

        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void whenClearCartCartNotFound_thenThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.clearCart(1L);
        });
    }

    @Test
    void whenGetAllCarts_thenReturnAllCarts() {
        when(cartRepository.findAll()).thenReturn(List.of(testCart));

        List<CartResponse> responses = cartService.getAllCarts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(cartRepository, times(1)).findAll();
    }
}
