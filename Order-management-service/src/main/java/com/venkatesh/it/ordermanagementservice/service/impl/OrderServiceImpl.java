package com.venkatesh.it.ordermanagementservice.service.impl;

import com.venkatesh.it.ordermanagementservice.dto.CreateOrderRequest;
import com.venkatesh.it.ordermanagementservice.dto.OrderItemRequest;
import com.venkatesh.it.ordermanagementservice.dto.ShippingAddressRequest;
import com.venkatesh.it.ordermanagementservice.dto.UpdateOrderRequest;
import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.entity.OrderItem;
import com.venkatesh.it.ordermanagementservice.entity.ShippingAddress;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import com.venkatesh.it.ordermanagementservice.enums.PaymentStatus;
import com.venkatesh.it.ordermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.ordermanagementservice.repository.OrderRepository;
import com.venkatesh.it.ordermanagementservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private static final AtomicInteger orderSequence = new AtomicInteger(1);
    
    @Override
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        Order order = Order.builder()
                .userId(request.getUserId())
                .customerId(request.getCustomerId())
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .subtotal(BigDecimal.ZERO)
                .shippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO)
                .tax(request.getTax() != null ? request.getTax() : BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .productId(itemRequest.getProductId())
                    .productName(itemRequest.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .total(itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();
            
            order.addOrderItem(orderItem);
            subtotal = subtotal.add(orderItem.getTotal());
        }
        
        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingCost()).add(order.getTax()));
        
        if (request.getShippingAddress() != null) {
            ShippingAddressRequest addrReq = request.getShippingAddress();
            ShippingAddress shippingAddress = ShippingAddress.builder()
                    .addressType(addrReq.getAddressType())
                    .addressLine1(addrReq.getAddressLine1())
                    .addressLine2(addrReq.getAddressLine2())
                    .city(addrReq.getCity())
                    .state(addrReq.getState())
                    .postalCode(addrReq.getPostalCode())
                    .country(addrReq.getCountry())
                    .phone(addrReq.getPhone())
                    .build();
            
            order.setShippingAddress(shippingAddress);
        }
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} and order number: {}", savedOrder.getId(), savedOrder.getOrderNumber());
        return savedOrder;
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        log.info("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order by order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        log.info("Fetching orders for customer ID: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Override
    public Order updateOrder(Long orderId, UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", orderId);
        Order order = getOrderById(orderId);
        
        if (request.getStatus() != null) {
            validateStatusTransition(order.getStatus(), request.getStatus());
            order.setStatus(request.getStatus());
        }
        
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} updated successfully", orderId);
        return updatedOrder;
    }
    
    @Override
    public Order cancelOrder(Long orderId) {
        log.info("Cancelling order with ID: {}", orderId);
        Order order = getOrderById(orderId);
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new IllegalStateException("Cannot cancel a delivered or refunded order");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
        return cancelledOrder;
    }
    
    @Override
    public void deleteOrder(Long orderId) {
        log.info("Deleting order with ID: {}", orderId);
        Order order = getOrderById(orderId);
        
        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot delete a confirmed or shipped order");
        }
        
        orderRepository.delete(order);
        log.info("Order {} deleted successfully", orderId);
    }
    
    private String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = orderSequence.getAndIncrement();
        return "ORD-" + datePrefix + "-" + String.format("%04d", sequence);
    }
    
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        log.info("Validating status transition from {} to {}", currentStatus, newStatus);
        
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != OrderStatus.REFUNDED) {
                    throw new IllegalStateException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            case CANCELLED:
            case REFUNDED:
                throw new IllegalStateException("Cannot change status from " + currentStatus);
        }
    }
}
