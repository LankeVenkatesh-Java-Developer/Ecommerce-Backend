package com.venkatesh.it.ordermanagementservice.dto;

import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String notes;
}
