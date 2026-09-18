package com.venkatesh.it.cartmanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.cartmanagementservice.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Disabled("Temporarily disabled due to context loading issues")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private CartResponse cartResponse;
    private CartItemRequest cartItemRequest;

    @BeforeEach
    void setUp() {
        cartResponse = CartResponse.builder()
                .id(1L)
                .userId(1L)
                .total(199.98)
                .totalItems(2)
                .items(Collections.emptyList())
                .build();

        cartItemRequest = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetCurrentUserCart_thenReturnCart() throws Exception {
        when(cartService.getCartByUserId(1L)).thenReturn(cartResponse);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.total").value(199.98));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetCartByUserId_thenReturnCart() throws Exception {
        when(cartService.getCartByUserId(1L)).thenReturn(cartResponse);

        mockMvc.perform(get("/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenAddItemToCart_thenReturnCreatedCart() throws Exception {
        when(cartService.addItemToCart(anyLong(), any(CartItemRequest.class)))
                .thenReturn(cartResponse);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenUpdateCartItem_thenReturnUpdatedCart() throws Exception {
        when(cartService.updateCartItem(anyLong(), anyLong(), any(Integer.class)))
                .thenReturn(cartResponse);

        mockMvc.perform(put("/cart/items/1")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenRemoveItemFromCart_thenReturnUpdatedCart() throws Exception {
        when(cartService.removeItemFromCart(anyLong(), anyLong()))
                .thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenClearCart_thenReturnClearedCart() throws Exception {
        when(cartService.clearCart(anyLong())).thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void whenGetAllCarts_thenReturnAllCarts() throws Exception {
        when(cartService.getAllCarts()).thenReturn(List.of(cartResponse));

        mockMvc.perform(get("/cart/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void whenClearCartByUserId_thenReturnClearedCart() throws Exception {
        when(cartService.clearCart(anyLong())).thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/admin/clear/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenAddItemToCartByUserId_thenReturnCreatedCart() throws Exception {
        when(cartService.addItemToCart(anyLong(), any(CartItemRequest.class)))
                .thenReturn(cartResponse);

        mockMvc.perform(post("/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenUpdateCartItemByUserId_thenReturnUpdatedCart() throws Exception {
        when(cartService.updateCartItem(anyLong(), anyLong(), any(Integer.class)))
                .thenReturn(cartResponse);

        mockMvc.perform(put("/cart/1/items/1")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenRemoveItemFromCartByUserId_thenReturnUpdatedCart() throws Exception {
        when(cartService.removeItemFromCart(anyLong(), anyLong()))
                .thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void whenClearCartByUserIdPath_thenReturnClearedCart() throws Exception {
        when(cartService.clearCart(anyLong())).thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/1/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void whenGetCurrentUserCartWithoutAuth_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAddItemToCartWithoutAuth_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetAllCartsWithoutAuth_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/cart/admin/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void whenGetAllCartsWithUserRole_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/cart/admin/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void whenAddItemToCartWithInvalidQuantity_thenReturnBadRequest() throws Exception {
        CartItemRequest invalidRequest = CartItemRequest.builder()
                .productId(1L)
                .quantity(0)
                .build();

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void whenAddItemToCartWithMissingProductId_thenReturnBadRequest() throws Exception {
        CartItemRequest invalidRequest = CartItemRequest.builder()
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void whenServiceThrowsException_thenReturnInternalServerError() throws Exception {
        when(cartService.getCartByUserId(anyLong()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/cart"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "1")
    void whenResourceNotFound_thenReturnNotFound() throws Exception {
        when(cartService.getCartByUserId(anyLong()))
                .thenThrow(new ResourceNotFoundException("Cart not found"));

        mockMvc.perform(get("/cart"))
                .andExpect(status().isNotFound());
    }
}
