package com.venkatesh.it.ordermanagementservice.Repository;

import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.entity.OrderItem;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .userId(1L)
                .customerId(1L)
                .orderNumber("ORD-001")
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .subtotal(new BigDecimal("100.00"))
                .total(new BigDecimal("121.00"))
                .build();
        testOrder = entityManager.persist(testOrder);

        testOrderItem = OrderItem.builder()
                .order(testOrder)
                .productId(1L)
                .productName("Test Product")
                .productSku("TEST-001")
                .productBrand("Test Brand")
                .quantity(2)
                .price(new BigDecimal("50.00"))
                .total(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void whenSaveOrderItem_thenReturnSavedOrderItem() {
        OrderItem savedOrderItem = orderItemRepository.save(testOrderItem);

        assertNotNull(savedOrderItem);
        assertNotNull(savedOrderItem.getId());
        assertEquals(testOrderItem.getProductId(), savedOrderItem.getProductId());
        assertEquals(testOrderItem.getProductName(), savedOrderItem.getProductName());
    }

    @Test
    void whenFindByOrderId_thenReturnOrderItems() {
        entityManager.persist(testOrderItem);
        entityManager.flush();

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(testOrder.getId());

        assertNotNull(orderItems);
        assertFalse(orderItems.isEmpty());
    }

    @Test
    void whenFindByOrderIdNotExists_thenReturnEmptyList() {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(999L);

        assertNotNull(orderItems);
        assertTrue(orderItems.isEmpty());
    }

    @Test
    void whenDeleteById_thenOrderItemDeleted() {
        OrderItem savedOrderItem = entityManager.persist(testOrderItem);
        entityManager.flush();

        orderItemRepository.deleteById(savedOrderItem.getId());

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(testOrder.getId());
        assertTrue(orderItems.isEmpty());
    }
}
