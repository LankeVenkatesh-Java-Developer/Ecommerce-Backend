package com.venkatesh.it.productsmanagementservice.repository;

import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.entity.CategoryStatus;
import com.venkatesh.it.productsmanagementservice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Electronics")
                .description("Electronic items")
                .active(true)
                .status(CategoryStatus.ACTIVE)
                .build();
        testCategory = entityManager.persist(testCategory);

        testProduct = Product.builder()
                .name("Test Product")
                .description("Test description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .sku("TEST-001")
                .brand("Test Brand")
                .deleted(false)
                .category(testCategory)
                .status(Product.ProductStatus.ACTIVE)
                .build();
    }

    @Test
    void whenSaveProduct_thenReturnSavedProduct() {
        Product savedProduct = productRepository.save(testProduct);

        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getSku(), savedProduct.getSku());
    }

    @Test
    void whenFindById_thenReturnProduct() {
        Product savedProduct = entityManager.persist(testProduct);
        entityManager.flush();

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
    }

    @Test
    void whenExistsBySku_thenReturnTrue() {
        entityManager.persist(testProduct);
        entityManager.flush();

        boolean exists = productRepository.existsBySku("TEST-001");

        assertTrue(exists);
    }

    @Test
    void whenExistsBySkuNotExists_thenReturnFalse() {
        boolean exists = productRepository.existsBySku("NON-EXISTENT");

        assertFalse(exists);
    }

    @Test
    void whenExistsBySkuAndIdNot_thenReturnTrue() {
        Product savedProduct = entityManager.persist(testProduct);
        entityManager.flush();

        Product anotherProduct = Product.builder()
                .name("Another Product")
                .description("Another description")
                .price(new BigDecimal("149.99"))
                .quantity(5)
                .sku("TEST-002")
                .brand("Another Brand")
                .deleted(false)
                .category(testCategory)
                .status(Product.ProductStatus.ACTIVE)
                .build();
        entityManager.persist(anotherProduct);
        entityManager.flush();

        boolean exists = productRepository.existsBySkuAndIdNot("TEST-002", savedProduct.getId());

        assertTrue(exists);
    }

    @Test
    void whenExistsBySkuAndIdNotSameProduct_thenReturnFalse() {
        Product savedProduct = entityManager.persist(testProduct);
        entityManager.flush();

        boolean exists = productRepository.existsBySkuAndIdNot("TEST-001", savedProduct.getId());

        assertFalse(exists);
    }

    @Test
    void whenDeleteById_thenProductDeleted() {
        Product savedProduct = entityManager.persist(testProduct);
        entityManager.flush();

        productRepository.deleteById(savedProduct.getId());

        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void whenFindAll_thenReturnAllProducts() {
        entityManager.persist(testProduct);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findAll(pageable);

        assertNotNull(products);
        assertTrue(products.getTotalElements() > 0);
    }
}
