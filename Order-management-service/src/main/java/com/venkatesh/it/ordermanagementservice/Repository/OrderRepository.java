package com.venkatesh.it.ordermanagementservice.repository;

import com.venkatesh.it.ordermanagementservice.entity.Order;
import com.venkatesh.it.ordermanagementservice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserId(Long userId);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    @Query("SELECT o.orderNumber FROM Order o WHERE o.orderNumber LIKE :prefix ORDER BY o.orderNumber DESC LIMIT 1")
    String findLastOrderNumberByDatePrefix(@Param("prefix") String prefix);
}
