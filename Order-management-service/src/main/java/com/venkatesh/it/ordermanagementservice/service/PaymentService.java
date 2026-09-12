package com.venkatesh.it.ordermanagementservice.service;

import com.venkatesh.it.ordermanagementservice.dto.PaymentRequest;
import com.venkatesh.it.ordermanagementservice.entity.Payment;

import java.math.BigDecimal;

public interface PaymentService {
    Payment createPayment(PaymentRequest request);
    Payment verifyPayment(String paymentId);
    Payment processRefund(Long paymentId, BigDecimal amount);
    Payment getPaymentByOrderId(Long orderId);
}
