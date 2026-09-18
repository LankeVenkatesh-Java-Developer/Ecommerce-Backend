package com.venkatesh.it.cartmanagementservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.cartmanagementservice.dto.CartItemRequest;
import com.venkatesh.it.cartmanagementservice.dto.CartResponse;
import com.venkatesh.it.cartmanagementservice.entity.Cart;
import com.venkatesh.it.cartmanagementservice.entity.CartItem;
import com.venkatesh.it.cartmanagementservice.feign.ProductsClient;
import com.venkatesh.it.cartmanagementservice.repository.CartItemRepository;
import com.venkatesh.it.cartmanagementservice.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @MockBean
    private ProductsClient productsClient;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndAddItemToCart() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.total").value(199.98));

        Cart cart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalItems());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndGetCart() throws Exception {
        Cart cart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        cart = cartRepository.save(cart);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndUpdateCartItem() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        Cart cart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        cart = cartRepository.save(cart);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();
        item = cartItemRepository.save(item);
        cart.addItem(item);
        cartRepository.save(cart);

        mockMvc.perform(put("/cart/items/1")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(5));

        Cart updatedCart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(5, updatedCart.getTotalItems());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndRemoveItemFromCart() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        Cart cart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        cart = cartRepository.save(cart);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();
        cartItemRepository.save(item);
        cart.addItem(item);
        cartRepository.save(cart);

        mockMvc.perform(delete("/cart/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));

        Cart updatedCart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(0, updatedCart.getItems().size());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndClearCart() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        Cart cart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        cart = cartRepository.save(cart);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();
        cartItemRepository.save(item);
        cart.addItem(item);
        cartRepository.save(cart);

        mockMvc.perform(delete("/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));

        Cart clearedCart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(0, clearedCart.getItems().size());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndAddMultipleItems() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product 1", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image1.jpg", "TEST-001", "Test Brand", null
        ));
        when(productsClient.getProductById(2L)).thenReturn(new ProductsClient.ProductDTO(
                2L, "Test Product 2", "Description", new BigDecimal("49.99"), 10, "ACTIVE",
                "http://test.com/image2.jpg", "TEST-002", "Test Brand", null
        ));

        CartItemRequest request1 = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        CartItemRequest request2 = CartItemRequest.builder()
                .productId(2L)
                .quantity(3)
                .build();

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        Cart cart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(2, cart.getItems().size());
        assertEquals(5, cart.getTotalItems());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndAddExistingItemUpdatesQuantity() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalItems").value(4));

        Cart cart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(1, cart.getItems().size());
        assertEquals(4, cart.getTotalItems());
    }

    @Test
    @WithMockUser(username = "1")
    void testEndToEndUpdateQuantityToZeroRemovesItem() throws Exception {
        when(productsClient.getProductById(1L)).thenReturn(new ProductsClient.ProductDTO(
                1L, "Test Product", "Description", new BigDecimal("99.99"), 10, "ACTIVE",
                "http://test.com/image.jpg", "TEST-001", "Test Brand", null
        ));

        Cart cart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        cart = cartRepository.save(cart);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();
        cartItemRepository.save(item);
        cart.addItem(item);
        cartRepository.save(cart);

        mockMvc.perform(put("/cart/items/1")
                        .param("quantity", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));

        Cart updatedCart = cartRepository.findByUserId(1L).orElseThrow();
        assertEquals(0, updatedCart.getItems().size());
    }

    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void testEndToEndGetAllCarts() throws Exception {
        Cart cart1 = Cart.builder()
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .totalItems(1)
                .build();
        cartRepository.save(cart1);

        Cart cart2 = Cart.builder()
                .userId(2L)
                .totalAmount(new BigDecimal("49.99"))
                .totalItems(1)
                .build();
        cartRepository.save(cart2);

        mockMvc.perform(get("/cart/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void testEndToEndAdminClearCart() throws Exception {
        Cart cart = Cart.builder()
                .userId(2L)
                .totalAmount(new BigDecimal("99.99"))
                .totalItems(1)
                .build();
        cart = cartRepository.save(cart);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(1)
                .price(new BigDecimal("99.99"))
                .build();
        cartItemRepository.save(item);
        cart.addItem(item);
        cartRepository.save(cart);

        mockMvc.perform(delete("/cart/admin/clear/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));

        Cart clearedCart = cartRepository.findByUserId(2L).orElseThrow();
        assertEquals(0, clearedCart.getItems().size());
    }

    private void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }
}
