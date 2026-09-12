package com.venkatesh.it.ordermanagementservice.service;

import com.venkatesh.it.ordermanagementservice.dto.PaymentRequest;
import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.entity.Payment;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import com.venkatesh.it.ordermanagementservice.exception.PaymentException;
import com.venkatesh.it.ordermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.ordermanagementservice.repository.OrderRepository;
import com.venkatesh.it.ordermanagementservice.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayPaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    public Payment processPayment(PaymentRequest request) {
        try {
            log.info("Processing payment for order: {}", request.getOrderId());
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(new BigDecimal("100")).intValue());
            orderRequest.put("currency", request.getCurrency().toUpperCase());
            orderRequest.put("receipt", "order_" + request.getOrderId());
            orderRequest.put("payment_capture", 1);
            
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            log.info("Razorpay order created with ID: {}", razorpayOrder.get("id").toString());
            
            return Payment.builder()
                    .paymentId(razorpayOrder.get("id"))
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .paymentMethod(request.getPaymentMethod())
                    .status(PaymentStatus.PENDING)
                    .transactionId(razorpayOrder.get("id"))
                    .paymentGatewayResponse(razorpayOrder.toString())
                    .build();
            
        } catch (RazorpayException e) {
            log.error("Payment processing failed: {}", e.getMessage());
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }
    
    @Transactional
    public Payment confirmPayment(String paymentId) {
        try {
            log.info("Confirming payment with ID: {}", paymentId);
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);
            log.info("Razorpay payment status: {}", razorpayPayment.get("status").toString());
            
            Payment payment = paymentRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
            
            if ("captured".equals(razorpayPayment.get("status"))) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(razorpayPayment.get("id"));
                payment.setPaymentGatewayResponse(razorpayPayment.toString());
                
                Order order = payment.getOrder();
                if (order != null) {
                    order.setPaymentStatus(PaymentStatus.COMPLETED);
                    order.setStatus(OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                    log.info("Order {} status updated to CONFIRMED", order.getId());
                }
            } else if ("failed".equals(razorpayPayment.get("status"))) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setPaymentGatewayResponse(razorpayPayment.toString());
                log.info("Payment {} marked as FAILED", paymentId);
            }
            
            return paymentRepository.save(payment);
            
        } catch (RazorpayException e) {
            log.error("Payment confirmation failed: {}", e.getMessage());
            throw new PaymentException("Payment confirmation failed: " + e.getMessage());
        }
    }
    
    @Transactional
    public Payment processRefund(Long paymentId, BigDecimal amount) {
        try {
            log.info("Processing refund for payment ID: {}, amount: {}", paymentId, amount);
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
            
            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                throw new IllegalStateException("Can only refund completed payments");
            }
            
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount.multiply(new BigDecimal("100")).intValue());
            
            com.razorpay.Refund refund = razorpayClient.payments.refund(payment.getPaymentId(), refundRequest);
            log.info("Refund processed with ID: {}", refund.get("id").toString());
            
            if (amount.compareTo(payment.getAmount()) == 0) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else {
                payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }
            
            payment.setPaymentGatewayResponse(refund.toString());
            return paymentRepository.save(payment);
            
        } catch (RazorpayException e) {
            log.error("Refund processing failed: {}", e.getMessage());
            throw new PaymentException("Refund processing failed: " + e.getMessage());
        }
    }
}
