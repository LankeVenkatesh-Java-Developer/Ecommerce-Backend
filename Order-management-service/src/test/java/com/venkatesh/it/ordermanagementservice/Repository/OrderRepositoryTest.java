package com.venkatesh.it.ordermanagementservice.Repository;

import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.repository.OrderRepository;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .userId(1L)
                .customerId(1L)
                .orderNumber("ORD-001")
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .subtotal(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .tax(new BigDecimal("11.00"))
                .total(new BigDecimal("121.00"))
                .build();
    }

    @Test
    void whenSaveOrder_thenReturnSavedOrder() {
        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getId());
        assertEquals(testOrder.getOrderNumber(), savedOrder.getOrderNumber());
        assertEquals(testOrder.getUserId(), savedOrder.getUserId());
    }

    @Test
    void whenFindByOrderNumber_thenReturnOrder() {
        entityManager.persist(testOrder);
        entityManager.flush();

        Optional<Order> foundOrder = orderRepository.findByOrderNumber("ORD-001");

        assertTrue(foundOrder.isPresent());
        assertEquals(testOrder.getOrderNumber(), foundOrder.get().getOrderNumber());
    }

    @Test
    void whenFindByOrderNumberNotExists_thenReturnEmpty() {
        Optional<Order> foundOrder = orderRepository.findByOrderNumber("NON-EXISTENT");

        assertFalse(foundOrder.isPresent());
    }

    @Test
    void whenFindByUserId_thenReturnOrders() {
        entityManager.persist(testOrder);
        entityManager.flush();

        List<Order> orders = orderRepository.findByUserId(1L);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void whenFindByCustomerId_thenReturnOrders() {
        entityManager.persist(testOrder);
        entityManager.flush();

        List<Order> orders = orderRepository.findByCustomerId(1L);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void whenFindByUserIdOrderByCreatedAtDesc_thenReturnOrders() {
        entityManager.persist(testOrder);
        entityManager.flush();

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(1L);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void whenFindByStatus_thenReturnOrders() {
        entityManager.persist(testOrder);
        entityManager.flush();

        List<Order> orders = orderRepository.findByStatus(OrderStatus.PENDING);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void whenFindByUserIdAndStatus_thenReturnOrders() {
        entityManager.persist(testOrder);
        entityManager.flush();

        List<Order> orders = orderRepository.findByUserIdAndStatus(1L, OrderStatus.PENDING);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    void whenDeleteById_thenOrderDeleted() {
        Order savedOrder = entityManager.persist(testOrder);
        entityManager.flush();

        orderRepository.deleteById(savedOrder.getId());

        Optional<Order> deletedOrder = orderRepository.findById(savedOrder.getId());
        assertFalse(deletedOrder.isPresent());
    }
}
