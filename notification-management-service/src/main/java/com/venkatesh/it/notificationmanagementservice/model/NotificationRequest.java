package com.venkatesh.it.notificationmanagementservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String recipientEmail;
    private String recipientPhone;
    private String customerName;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private String orderId;
    private String orderDetails;
    private String offerDetails;
}
