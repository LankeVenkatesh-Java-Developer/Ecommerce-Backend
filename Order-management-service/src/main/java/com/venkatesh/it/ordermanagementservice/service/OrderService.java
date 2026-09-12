package com.venkatesh.it.ordermanagementservice.service;

import com.venkatesh.it.ordermanagementservice.dto.CreateOrderRequest;
import com.venkatesh.it.ordermanagementservice.dto.UpdateOrderRequest;
import com.venkatesh.it.ordermanagementservice.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    Order getOrderById(Long orderId);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getOrdersByUserId(Long userId);
    List<Order> getOrdersByCustomerId(Long customerId);
    Order updateOrder(Long orderId, UpdateOrderRequest request);
    Order cancelOrder(Long orderId);
    void deleteOrder(Long orderId);
}
