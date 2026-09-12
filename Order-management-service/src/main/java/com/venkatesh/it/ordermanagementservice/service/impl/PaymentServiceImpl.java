package com.venkatesh.it.ordermanagementservice.service.impl;

import com.venkatesh.it.ordermanagementservice.dto.PaymentRequest;
import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.entity.Payment;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import com.venkatesh.it.ordermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.ordermanagementservice.repository.OrderRepository;
import com.venkatesh.it.ordermanagementservice.repository.PaymentRepository;
import com.venkatesh.it.ordermanagementservice.service.PaymentService;
import com.venkatesh.it.ordermanagementservice.service.RazorpayPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayPaymentService razorpayPaymentService;
    
    @Override
    public Payment createPayment(PaymentRequest request) {
        log.info("Creating payment for order: {}", request.getOrderId());
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));
        
        Payment payment = razorpayPaymentService.processPayment(request);
        payment.setOrder(order);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());
        return savedPayment;
    }
    
    @Override
    public Payment verifyPayment(String paymentId) {
        log.info("Verifying payment with ID: {}", paymentId);
        return razorpayPaymentService.confirmPayment(paymentId);
    }
    
    @Override
    public Payment processRefund(Long paymentId, BigDecimal amount) {
        log.info("Processing refund for payment ID: {}, amount: {}", paymentId, amount);
        return razorpayPaymentService.processRefund(paymentId, amount);
    }
    
    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for order ID: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
    }
}
