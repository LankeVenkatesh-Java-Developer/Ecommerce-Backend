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
        Page<Product> products = productRepository.findAll(
            (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), false),
            pageable
        );
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

        // Check SKU uniqueness if provided
        String sku = requestDTO.getSku();
        if (sku != null && !sku.trim().isEmpty()) {
            if (productRepository.existsBySku(sku)) {
                throw new IllegalArgumentException("Product with SKU '" + sku + "' already exists");
            }
        }

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setCategory(category);
        product.setImageUrl(requestDTO.getImageUrl());
        product.setBrand(requestDTO.getBrand());
        product.setStatus(requestDTO.getQuantity() > 0 ? Product.ProductStatus.ACTIVE : Product.ProductStatus.OUT_OF_STOCK);

        // Auto-generate SKU if not provided
        if (sku == null || sku.trim().isEmpty()) {
            product.setSku(generateSku(requestDTO.getName()));
        } else {
            product.setSku(sku);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        // Check SKU uniqueness if being changed
        String newSku = requestDTO.getSku();
        if (newSku != null && !newSku.trim().isEmpty() && !newSku.equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(newSku, id)) {
                throw new IllegalArgumentException("Product with SKU '" + newSku + "' already exists");
            }
            product.setSku(newSku);
        }

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setCategory(category);
        product.setImageUrl(requestDTO.getImageUrl());
        product.setBrand(requestDTO.getBrand());

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

        // Use soft delete to preserve historical order data
        product.setDeleted(true);
        product.setStatus(Product.ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    public ProductResponseDTO updateProductStatus(Long id, Product.ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    public ProductResponseDTO updateProductStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new IllegalStateException("Insufficient stock. Cannot deduct more than available.");
        }
        
        product.setQuantity(newQuantity);
        if (newQuantity > 0) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        } else {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }
        
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
                product.getImageUrl(),
                product.getSku(),
                product.getBrand()
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
                product.getSku(),
                product.getBrand(),
                categoryDTO,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private String generateSku(String productName) {
        // Generate SKU from product name: take first 3 letters, convert to uppercase, add timestamp
        String prefix = productName.replaceAll("[^a-zA-Z]", "").toUpperCase();
        if (prefix.length() > 3) {
            prefix = prefix.substring(0, 3);
        } else if (prefix.isEmpty()) {
            prefix = "PRD";
        }
        long timestamp = System.currentTimeMillis() % 10000;
        return prefix + "-" + timestamp;
    }
}
