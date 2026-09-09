package com.venkatesh.it.productsmanagementservice.service;

import com.venkatesh.it.productsmanagementservice.dto.CategoryDTO;
import com.venkatesh.it.productsmanagementservice.dto.CategoryRequestDTO;
import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.productsmanagementservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic devices");
        category.setImageUrl("https://example.com/image.jpg");
        category.setActive(true);

        categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Electronics");
        categoryRequestDTO.setDescription("Electronic devices");
        categoryRequestDTO.setActive(true);
    }

    @Test
    void getAllCategories_ShouldReturnActiveCategories() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Furniture");
        category2.setActive(false);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void createCategory_WhenNameNotExists_ShouldCreateCategory() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.createCategory(categoryRequestDTO);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).existsByName("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WhenNameExists_ShouldThrowException() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(categoryRequestDTO));
        verify(categoryRepository, times(1)).existsByName("Electronics");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryExistsAndNameNotTaken_ShouldUpdateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.updateCategory(1L, categoryRequestDTO);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, categoryRequestDTO));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExistsAndNoProducts_ShouldDeleteCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
