package com.venkatesh.it.productsmanagementservice.service;

import com.venkatesh.it.productsmanagementservice.dto.CategoryDTO;
import com.venkatesh.it.productsmanagementservice.dto.CategoryRequestDTO;
import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.entity.CategoryStatus;
import com.venkatesh.it.productsmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.productsmanagementservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getActive())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getAllCategoriesForAdmin() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    public CategoryDTO createCategory(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Category with name '" + requestDTO.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setActive(requestDTO.getActive() != null ? requestDTO.getActive() : true);
        category.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(requestDTO.getName()) && 
            categoryRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Category with name '" + requestDTO.getName() + "' already exists");
        }

        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setActive(requestDTO.getActive());
        category.setStatus(requestDTO.getStatus());

        Category updatedCategory = categoryRepository.save(category);
        return mapToDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }

    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                category.getActive(),
                category.getStatus()
        );
    }
}
