package com.venkatesh.it.productsmanagementservice.service;

import com.venkatesh.it.productsmanagementservice.dto.*;
import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.entity.Product;
import com.venkatesh.it.productsmanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.productsmanagementservice.repository.CategoryRepository;
import com.venkatesh.it.productsmanagementservice.repository.ProductRepository;
import com.venkatesh.it.productsmanagementservice.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductListDTO> getProducts(Long categoryId, String search, Product.ProductStatus status, Pageable pageable) {
        if (categoryId != null) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        }

        Page<Product> products = productRepository.findAll(
                ProductSpecification.withFilters(categoryId, search, status),
                pageable
        );

        return products.map(this::mapToListDTO);
    }

    public Page<ProductListDTO> getAllProductsForAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToListDTO);
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponseDTO(product);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setCategory(category);
        product.setImageUrl(requestDTO.getImageUrl());
        product.setStatus(requestDTO.getQuantity() > 0 ? Product.ProductStatus.ACTIVE : Product.ProductStatus.OUT_OF_STOCK);

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setCategory(category);
        product.setImageUrl(requestDTO.getImageUrl());

        if (requestDTO.getQuantity() > 0) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        } else {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductResponseDTO updateProductStatus(Long id, Product.ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    private ProductListDTO mapToListDTO(Product product) {
        return new ProductListDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getStatus().name(),
                product.getImageUrl()
        );
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        CategoryDTO categoryDTO = new CategoryDTO(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription(),
                product.getCategory().getImageUrl(),
                product.getCategory().getActive(),
                product.getCategory().getStatus()
        );

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getStatus().name(),
                product.getImageUrl(),
                categoryDTO,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
