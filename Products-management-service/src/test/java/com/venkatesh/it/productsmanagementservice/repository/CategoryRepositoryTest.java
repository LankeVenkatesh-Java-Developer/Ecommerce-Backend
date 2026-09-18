package com.venkatesh.it.productsmanagementservice.repository;

import com.venkatesh.it.productsmanagementservice.entity.Category;
import com.venkatesh.it.productsmanagementservice.entity.CategoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Electronics")
                .description("Electronic items")
                .active(true)
                .status(CategoryStatus.ACTIVE)
                .build();
    }

    @Test
    void whenSaveCategory_thenReturnSavedCategory() {
        Category savedCategory = categoryRepository.save(testCategory);

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());
        assertEquals(testCategory.getName(), savedCategory.getName());
        assertEquals(testCategory.getDescription(), savedCategory.getDescription());
    }

    @Test
    void whenFindById_thenReturnCategory() {
        Category savedCategory = entityManager.persist(testCategory);
        entityManager.flush();

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory.getId(), foundCategory.get().getId());
    }

    @Test
    void whenFindByName_thenReturnCategory() {
        entityManager.persist(testCategory);
        entityManager.flush();

        Optional<Category> foundCategory = categoryRepository.findByName("Electronics");

        assertTrue(foundCategory.isPresent());
        assertEquals(testCategory.getName(), foundCategory.get().getName());
    }

    @Test
    void whenFindByNameNotExists_thenReturnEmpty() {
        Optional<Category> foundCategory = categoryRepository.findByName("NonExistent");

        assertFalse(foundCategory.isPresent());
    }

    @Test
    void whenExistsByName_thenReturnTrue() {
        entityManager.persist(testCategory);
        entityManager.flush();

        boolean exists = categoryRepository.existsByName("Electronics");

        assertTrue(exists);
    }

    @Test
    void whenExistsByNameNotExists_thenReturnFalse() {
        boolean exists = categoryRepository.existsByName("NonExistent");

        assertFalse(exists);
    }

    @Test
    void whenDeleteById_thenCategoryDeleted() {
        Category savedCategory = entityManager.persist(testCategory);
        entityManager.flush();

        categoryRepository.deleteById(savedCategory.getId());

        Optional<Category> deletedCategory = categoryRepository.findById(savedCategory.getId());
        assertFalse(deletedCategory.isPresent());
    }

    @Test
    void whenFindAll_thenReturnAllCategories() {
        entityManager.persist(testCategory);
        entityManager.flush();

        var categories = categoryRepository.findAll();

        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }
}
