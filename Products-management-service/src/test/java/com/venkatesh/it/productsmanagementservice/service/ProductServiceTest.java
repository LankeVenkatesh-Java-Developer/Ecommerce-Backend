package com.venkatesh.it.productsmanagementservice.service;

import com.venkatesh.it.productsmanagementservice.dto.ProductListDTO;
import com.venkatesh.it.productsmanagementservice.dto.ProductRequestDTO;
import com.venkatesh.it.productsmanagementservice.dto.ProductResponseDTO;
import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.entity.Product;
import com.venkatesh.it.productsmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.productsmanagementservice.repository.CategoryRepository;
import com.venkatesh.it.productsmanagementservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductRequestDTO productRequestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setActive(true);

        product = new Product();
        product.setId(1L);
        product.setName("iPhone 16");
        product.setDescription("Apple smartphone");
        product.setPrice(new BigDecimal("79999.00"));
        product.setQuantity(20);
        product.setCategory(category);
        product.setStatus(Product.ProductStatus.ACTIVE);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("iPhone 16");
        productRequestDTO.setDescription("Apple smartphone");
        productRequestDTO.setPrice(new BigDecimal("79999.00"));
        productRequestDTO.setQuantity(20);
        productRequestDTO.setCategoryId(1L);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getProducts_WithoutFilters_ShouldReturnAllProducts() {
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        Page<ProductListDTO> result = productService.getProducts(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("iPhone 16", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProducts_WithValidCategoryId_ShouldReturnFilteredProducts() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        Page<ProductListDTO> result = productService.getProducts(1L, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProducts_WithInvalidCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProducts(999L, null, null, pageable));
        verify(categoryRepository, times(1)).findById(999L);
        verify(productRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProducts_WithSearchKeyword_ShouldReturnFilteredProducts() {
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        Page<ProductListDTO> result = productService.getProducts(null, "iPhone", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("iPhone 16", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_WhenCategoryExists_ShouldCreateProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("iPhone 16", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_WhenCategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productRequestDTO));
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductAndCategoryExist_ShouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.updateProduct(1L, productRequestDTO);

        assertNotNull(result);
        assertEquals("iPhone 16", result.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productRequestDTO));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void updateProductStatus_WhenProductExists_ShouldUpdateStatus() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.updateProductStatus(1L, Product.ProductStatus.INACTIVE);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProductStatus_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductStatus(1L, Product.ProductStatus.INACTIVE));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
