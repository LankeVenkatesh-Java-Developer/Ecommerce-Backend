package com.venkatesh.it.productsmanagementservice.controller;

import com.venkatesh.it.productsmanagementservice.dto.CategoryDTO;
import com.venkatesh.it.productsmanagementservice.dto.CategoryRequestDTO;
import com.venkatesh.it.productsmanagementservice.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");
        categoryDTO.setDescription("Electronic devices");
        categoryDTO.setImageUrl("https://example.com/image.jpg");
        categoryDTO.setActive(true);
    }

    @Test
    void getAllCategories_ShouldReturnCategories() {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(categoryDTO));

        var response = categoryController.getAllCategories();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(categoryService, times(1)).getAllCategories();
    }
}
