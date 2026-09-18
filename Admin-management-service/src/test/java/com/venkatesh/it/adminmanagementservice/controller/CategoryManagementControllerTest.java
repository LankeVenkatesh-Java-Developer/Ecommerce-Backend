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

@WebMvcTest(CategoryManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryManagementControllerTest {

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
    void whenGetAllCategoriesForAdmin_thenReturnOk() throws Exception {
        mockMvc.perform(get("/categories/admin/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetAllCategoriesForAdminWithSuperAdmin_thenReturnOk() throws Exception {
        mockMvc.perform(get("/categories/admin/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetAllCategories_thenReturnOk() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetCategoryById_thenReturnOk() throws Exception {
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateCategory_thenReturnOk() throws Exception {
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("name", "Electronics");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Electronics\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateCategory_thenReturnOk() throws Exception {
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Electronics\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenDeleteCategory_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isOk());
    }
}
