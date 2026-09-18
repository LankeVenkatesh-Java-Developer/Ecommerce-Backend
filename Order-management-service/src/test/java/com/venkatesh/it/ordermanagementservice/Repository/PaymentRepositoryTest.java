package com.venkatesh.it.ordermanagementservice.repository;

import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.entity.Payment;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private com.venkatesh.it.ordermanagementservice.repository.PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Order testOrder;
    private Payment testPayment;

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

        testPayment = Payment.builder()
                .order(testOrder)
                .paymentId("PAY-001")
                .amount(new BigDecimal("121.00"))
                .currency("USD")
                .paymentMethod("RAZORPAY")
                .status(PaymentStatus.COMPLETED)
                .transactionId("TXN-001")
                .build();
    }

    @Test
    void whenSavePayment_thenReturnSavedPayment() {
        Payment savedPayment = paymentRepository.save(testPayment);

        assertNotNull(savedPayment);
        assertNotNull(savedPayment.getId());
        assertEquals(testPayment.getPaymentId(), savedPayment.getPaymentId());
        assertEquals(testPayment.getAmount(), savedPayment.getAmount());
    }

    @Test
    void whenFindById_thenReturnPayment() {
        Payment savedPayment = entityManager.persist(testPayment);
        entityManager.flush();

        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        assertTrue(foundPayment.isPresent());
        assertEquals(savedPayment.getId(), foundPayment.get().getId());
    }

    @Test
    void whenFindByPaymentId_thenReturnPayment() {
        entityManager.persist(testPayment);
        entityManager.flush();

        Optional<Payment> foundPayment = paymentRepository.findByPaymentId("PAY-001");

        assertTrue(foundPayment.isPresent());
        assertEquals(testPayment.getPaymentId(), foundPayment.get().getPaymentId());
    }

    @Test
    void whenFindByPaymentIdNotExists_thenReturnEmpty() {
        Optional<Payment> foundPayment = paymentRepository.findByPaymentId("NON-EXISTENT");

        assertFalse(foundPayment.isPresent());
    }

    @Test
    void whenDeleteById_thenPaymentDeleted() {
        Payment savedPayment = entityManager.persist(testPayment);
        entityManager.flush();

        paymentRepository.deleteById(savedPayment.getId());

        Optional<Payment> deletedPayment = paymentRepository.findById(savedPayment.getId());
        assertFalse(deletedPayment.isPresent());
    }
}
