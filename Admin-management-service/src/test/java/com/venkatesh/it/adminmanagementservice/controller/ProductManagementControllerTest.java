package com.venkatesh.it.adminmanagementservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        when(restTemplate.exchange(any(String.class), any(), any(), eq(Object.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(new HashMap<>(), org.springframework.http.HttpStatus.OK));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetAllProductsForAdmin_thenReturnOk() throws Exception {
        mockMvc.perform(get("/products/admin/all")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetProductsWithFilters_thenReturnOk() throws Exception {
        mockMvc.perform(get("/products")
                        .param("categoryId", "1")
                        .param("search", "laptop")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetProductById_thenReturnOk() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateProduct_thenReturnOk() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"price\":999.99}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateProduct_thenReturnOk() throws Exception {
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Laptop\",\"price\":899.99}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenDeleteProduct_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateProductStatus_thenReturnOk() throws Exception {
        mockMvc.perform(patch("/products/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateProductStock_thenReturnOk() throws Exception {
        mockMvc.perform(put("/products/1/stock")
                        .param("quantity", "50"))
                .andExpect(status().isOk());
    }
}
