package com.venkatesh.it.cartmanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.service.CartService;
import org.junit.jupiter.api.BeforeEach;
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
}
