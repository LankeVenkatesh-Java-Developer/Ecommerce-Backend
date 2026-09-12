package com.venkatesh.it.ordermanagementservice.controller;

import com.venkatesh.it.ordermanagementservice.dto.PaymentRequest;
import com.venkatesh.it.ordermanagementservice.entity.Payment;
import com.venkatesh.it.ordermanagementservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @GetMapping("/{paymentId}/verify")
    public ResponseEntity<Payment> verifyPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.verifyPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }
    
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> processRefund(
            @PathVariable Long paymentId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        Payment payment = paymentService.processRefund(paymentId, amount);
        return ResponseEntity.ok(payment);
    }
}
