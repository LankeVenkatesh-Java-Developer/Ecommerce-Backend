package com.venkatesh.it.cartmanagementservice.repository;

import com.venkatesh.it.cartmanagementservice.entity.Cart;
import com.venkatesh.it.cartmanagementservice.entity.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .userId(1L)
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();
        testCart = entityManager.persist(testCart);

        testCartItem = CartItem.builder()
                .cart(testCart)
                .productId(1L)
                .productName("Test Product")
                .productImageUrl("http://test.com/image.jpg")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();
    }

    @Test
    void whenSaveCartItem_thenReturnSavedCartItem() {
        CartItem savedItem = cartItemRepository.save(testCartItem);

        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals(testCartItem.getProductId(), savedItem.getProductId());
        assertEquals(testCartItem.getProductName(), savedItem.getProductName());
        assertEquals(testCartItem.getQuantity(), savedItem.getQuantity());
    }

    @Test
    void whenFindByCartIdAndProductId_thenReturnCartItem() {
        entityManager.persist(testCartItem);
        entityManager.flush();

        Optional<CartItem> foundItem = cartItemRepository.findByCartIdAndProductId(
                testCart.getId(), testCartItem.getProductId()
        );

        assertTrue(foundItem.isPresent());
        assertEquals(testCartItem.getProductId(), foundItem.get().getProductId());
        assertEquals(testCartItem.getProductName(), foundItem.get().getProductName());
    }

    @Test
    void whenFindByCartIdAndProductIdNotExists_thenReturnEmpty() {
        Optional<CartItem> foundItem = cartItemRepository.findByCartIdAndProductId(999L, 999L);

        assertFalse(foundItem.isPresent());
    }

    @Test
    void whenDeleteByCartId_thenAllCartItemsDeleted() {
        entityManager.persist(testCartItem);
        entityManager.flush();

        cartItemRepository.deleteByCartId(testCart.getId());

        Optional<CartItem> deletedItem = cartItemRepository.findByCartIdAndProductId(
                testCart.getId(), testCartItem.getProductId()
        );
        assertFalse(deletedItem.isPresent());
    }

    @Test
    void whenCalculateTotal_thenTotalIsCalculated() {
        CartItem savedItem = entityManager.persist(testCartItem);
        entityManager.flush();

        assertNotNull(savedItem.getTotal());
        assertEquals(new BigDecimal("199.98"), savedItem.getTotal());
    }
}
