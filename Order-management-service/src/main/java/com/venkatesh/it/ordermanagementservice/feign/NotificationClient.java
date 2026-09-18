package com.venkatesh.it.ordermanagementservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-management-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/order-created")
    NotificationResponse sendOrderCreated(@RequestBody NotificationRequest request);

    @PostMapping("/api/notifications/order-delivered")
    NotificationResponse sendOrderDelivered(@RequestBody NotificationRequest request);

    @PostMapping("/api/notifications/order-cancelled")
    NotificationResponse sendOrderCancelled(@RequestBody NotificationRequest request);

    record NotificationRequest(
            String recipientEmail,
            String recipientPhone,
            String recipientName,
            String orderNumber,
            String notificationType,
            String channel
    ) {}

    record NotificationResponse(
            boolean success,
            String message,
            String emailStatus,
            String whatsappStatus
    ) {}
}
