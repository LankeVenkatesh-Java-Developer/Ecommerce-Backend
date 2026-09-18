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
import com.venkatesh.it.ordermanagementservice.feign.CartClient;
import com.venkatesh.it.ordermanagementservice.feign.NotificationClient;
import com.venkatesh.it.ordermanagementservice.feign.ProductsClient;
import com.venkatesh.it.ordermanagementservice.repository.OrderRepository;
import com.venkatesh.it.ordermanagementservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

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
    private final ProductsClient productsClient;
    private final CartClient cartClient;
    private final NotificationClient notificationClient;
    private static final AtomicInteger orderSequence = new AtomicInteger(1);
    
    @Override
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }
    
    @Override
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        // Validate and check inventory for all products
        for (OrderItemRequest itemRequest : request.getItems()) {
            try {
                ProductsClient.ProductDTO product = productsClient.getProductById(itemRequest.getProductId());
                if (product == null) {
                    throw new ResourceNotFoundException("Product not found: " + itemRequest.getProductId());
                }
                if (product.quantity() < itemRequest.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product: " + product.name() +
                            ". Available: " + product.quantity() + ", Requested: " + itemRequest.getQuantity());
                }
                // Validate product is active and not deleted
                if (!"ACTIVE".equalsIgnoreCase(product.status())) {
                    throw new IllegalStateException("Product is not available: " + product.name());
                }
                log.info("Product {} has sufficient stock: {}", product.name(), product.quantity());
            } catch (RestClientException e) {
                log.error("Failed to validate product inventory for product ID: {}", itemRequest.getProductId(), e);
                throw new IllegalStateException("Unable to validate product inventory. Please try again later.");
            }
        }
        
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
            // Get product details from service to validate price and get snapshot data
            try {
                ProductsClient.ProductDTO product = productsClient.getProductById(itemRequest.getProductId());

                // Validate price matches current product price
                if (itemRequest.getPrice() == null || itemRequest.getPrice().compareTo(product.price()) != 0) {
                    log.warn("Price mismatch for product {}. Using current price: {}", product.name(), product.price());
                }

                OrderItem orderItem = OrderItem.builder()
                        .productId(itemRequest.getProductId())
                        .productName(product.name())
                        .productSku(product.sku())
                        .productBrand(product.brand())
                        .quantity(itemRequest.getQuantity())
                        .price(product.price()) // Use actual product price, not frontend
                        .total(product.price().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                        .build();

                order.addOrderItem(orderItem);
                subtotal = subtotal.add(orderItem.getTotal());
            } catch (RestClientException e) {
                log.error("Failed to fetch product details for product ID: {}", itemRequest.getProductId(), e);
                throw new IllegalStateException("Unable to fetch product details. Please try again later.");
            }
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

        // Clear cart after successful order creation
        try {
            cartClient.clearCart(request.getUserId());
            log.info("Cart cleared for user: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Failed to clear cart for user: {}", request.getUserId(), e);
            // Don't fail order creation if cart clear fails
        }

        // Send order created notification
        try {
            NotificationClient.NotificationRequest notificationRequest = new NotificationClient.NotificationRequest(
                    "customer@example.com", // TODO: Get from user service
                    null, // TODO: Get from user service
                    "Customer",
                    savedOrder.getOrderNumber(),
                    "ORDER_CREATED",
                    "BOTH"
            );
            notificationClient.sendOrderCreated(notificationRequest);
            log.info("Order created notification sent for order: {}", savedOrder.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send order created notification for order: {}", savedOrder.getOrderNumber(), e);
            // Don't fail order creation if notification fails
        }

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

            // Deduct inventory when order is confirmed
            if (order.getStatus() == OrderStatus.PENDING && request.getStatus() == OrderStatus.CONFIRMED) {
                for (OrderItem item : order.getItems()) {
                    try {
                        productsClient.updateProductStock(item.getProductId(), -item.getQuantity());
                        log.info("Deducted {} units of product {} from inventory", item.getQuantity(), item.getProductId());
                    } catch (RestClientException e) {
                        log.error("Failed to deduct inventory for product ID: {}", item.getProductId(), e);
                        throw new IllegalStateException("Failed to update inventory. Order cannot be confirmed.");
                    }
                }
            }

            order.setStatus(request.getStatus());

            // Send notification for status changes
            try {
                if (request.getStatus() == OrderStatus.DELIVERED) {
                    NotificationClient.NotificationRequest notificationRequest = new NotificationClient.NotificationRequest(
                            "customer@example.com", // TODO: Get from user service
                            null, // TODO: Get from user service
                            "Customer",
                            order.getOrderNumber(),
                            "ORDER_DELIVERED",
                            "BOTH"
                    );
                    notificationClient.sendOrderDelivered(notificationRequest);
                    log.info("Order delivered notification sent for order: {}", order.getOrderNumber());
                }
            } catch (Exception e) {
                log.error("Failed to send order status notification for order: {}", order.getOrderNumber(), e);
            }
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
        
        // Restore inventory for all items in the order
        for (OrderItem item : order.getItems()) {
            try {
                productsClient.updateProductStock(item.getProductId(), item.getQuantity());
                log.info("Restored {} units of product {} to inventory", item.getQuantity(), item.getProductId());
            } catch (RestClientException e) {
                log.error("Failed to restore inventory for product ID: {}", item.getProductId(), e);
                // Continue with cancellation even if inventory restore fails
                // This should be handled by a compensation mechanism in production
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);

        // Send cancellation notification
        try {
            NotificationClient.NotificationRequest notificationRequest = new NotificationClient.NotificationRequest(
                    "customer@example.com", // TODO: Get from user service
                    null, // TODO: Get from user service
                    "Customer",
                    order.getOrderNumber(),
                    "ORDER_CANCELLED",
                    "BOTH"
            );
            notificationClient.sendOrderCancelled(notificationRequest);
            log.info("Order cancelled notification sent for order: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send order cancelled notification for order: {}", order.getOrderNumber(), e);
        }

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
        
        // Find the last order number for today
        String lastOrderNumber = orderRepository.findLastOrderNumberByDatePrefix("ORD-" + datePrefix + "-");
        
        int sequence = 1;
        if (lastOrderNumber != null) {
            try {
                String lastSequence = lastOrderNumber.substring(lastOrderNumber.lastIndexOf("-") + 1);
                sequence = Integer.parseInt(lastSequence) + 1;
            } catch (Exception e) {
                log.warn("Failed to parse last order number, starting from 1", e);
            }
        }
        
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
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from PROCESSING to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("Invalid status transition from OUT_FOR_DELIVERY to " + newStatus);
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
