package com.venkatesh.it.cartmanagementservice.repository;

import com.venkatesh.it.cartmanagementservice.entity.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .userId(1L)
                .totalAmount(java.math.BigDecimal.ZERO)
                .totalItems(0)
                .build();
    }

    @Test
    void whenSaveCart_thenReturnSavedCart() {
        Cart savedCart = cartRepository.save(testCart);
        
        assertNotNull(savedCart);
        assertNotNull(savedCart.getId());
        assertEquals(testCart.getUserId(), savedCart.getUserId());
        assertEquals(testCart.getTotalAmount(), savedCart.getTotalAmount());
        assertEquals(testCart.getTotalItems(), savedCart.getTotalItems());
    }

    @Test
    void whenFindByUserId_thenReturnCart() {
        entityManager.persist(testCart);
        entityManager.flush();

        Optional<Cart> foundCart = cartRepository.findByUserId(testCart.getUserId());

        assertTrue(foundCart.isPresent());
        assertEquals(testCart.getUserId(), foundCart.get().getUserId());
    }

    @Test
    void whenFindByUserIdNotExists_thenReturnEmpty() {
        Optional<Cart> foundCart = cartRepository.findByUserId(999L);

        assertFalse(foundCart.isPresent());
    }

    @Test
    void whenDeleteByUserId_thenCartDeleted() {
        entityManager.persist(testCart);
        entityManager.flush();

        cartRepository.deleteByUserId(testCart.getUserId());

        Optional<Cart> deletedCart = cartRepository.findByUserId(testCart.getUserId());
        assertFalse(deletedCart.isPresent());
    }

    @Test
    void whenFindById_thenReturnCart() {
        Cart savedCart = entityManager.persist(testCart);
        entityManager.flush();

        Optional<Cart> foundCart = cartRepository.findById(savedCart.getId());

        assertTrue(foundCart.isPresent());
        assertEquals(savedCart.getId(), foundCart.get().getId());
    }

    @Test
    void whenDeleteById_thenCartDeleted() {
        Cart savedCart = entityManager.persist(testCart);
        entityManager.flush();

        cartRepository.deleteById(savedCart.getId());

        Optional<Cart> deletedCart = cartRepository.findById(savedCart.getId());
        assertFalse(deletedCart.isPresent());
    }
}
