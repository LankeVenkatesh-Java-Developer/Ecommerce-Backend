package com.venkatesh.it.ordermanagementservice.dto;

import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long userId; // Will be overridden by authenticated user

    private Long customerId;

    @NotNull(message = "Items are required")
    @NotEmpty(message = "Items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private BigDecimal subtotal;

    private BigDecimal shippingCost = BigDecimal.ZERO;

    private BigDecimal tax = BigDecimal.ZERO;

    private BigDecimal total;

    private String notes;
}
