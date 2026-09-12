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
    @NotNull(message = "User ID is required")
    private Long userId;
    
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
    
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
    private BigDecimal subtotal;
    
    private BigDecimal shippingCost = BigDecimal.ZERO;
    
    private BigDecimal tax = BigDecimal.ZERO;
    
    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.01", message = "Total must be greater than 0")
    private BigDecimal total;
    
    private String notes;
}
