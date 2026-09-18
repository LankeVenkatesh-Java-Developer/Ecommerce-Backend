package com.venkatesh.it.cartmanagementservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidCartItemRequest_thenNoViolations() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(2)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenProductIdIsNull_thenViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(null)
                .quantity(2)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("productId", violations.iterator().next().getPropertyPath().toString());
        assertTrue(violations.iterator().next().getMessage().contains("required"));
    }

    @Test
    void whenQuantityIsNull_thenViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(null)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertTrue(violations.iterator().next().getMessage().contains("required"));
    }

    @Test
    void whenQuantityIsZero_thenViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(0)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertTrue(violations.iterator().next().getMessage().contains("at least 1"));
    }

    @Test
    void whenQuantityIsNegative_thenViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(-1)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertTrue(violations.iterator().next().getMessage().contains("at least 1"));
    }

    @Test
    void whenQuantityExceedsMax_thenViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(101)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertTrue(violations.iterator().next().getMessage().contains("cannot exceed 100"));
    }

    @Test
    void whenQuantityIsExactly100_thenNoViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(100)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenQuantityIsExactly1_thenNoViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(1)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenBothFieldsNull_thenTwoViolations() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(null)
                .quantity(null)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void whenAllFieldsInvalid_thenMultipleViolations() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(null)
                .quantity(0)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void whenBoundaryValue50_thenNoViolation() {
        CartItemRequest request = CartItemRequest.builder()
                .productId(1L)
                .quantity(50)
                .build();

        Set<ConstraintViolation<CartItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
