package com.venkatesh.it.notificationmanagementservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRequestTest {

    @Test
    void whenNoArgsConstructor_thenObjectCreated() {
        NotificationRequest request = new NotificationRequest();
        assertNotNull(request);
    }

    @Test
    void whenAllArgsConstructor_thenAllFieldsSet() {
        NotificationRequest request = new NotificationRequest(
                "test@example.com",
                "+1234567890",
                "John Doe",
                NotificationType.ORDER_CREATED,
                NotificationChannel.EMAIL,
                "ORD-001",
                "Test order details",
                "50% off"
        );

        assertEquals("test@example.com", request.getRecipientEmail());
        assertEquals("+1234567890", request.getRecipientPhone());
        assertEquals("John Doe", request.getCustomerName());
        assertEquals(NotificationType.ORDER_CREATED, request.getNotificationType());
        assertEquals(NotificationChannel.EMAIL, request.getChannel());
        assertEquals("ORD-001", request.getOrderId());
        assertEquals("Test order details", request.getOrderDetails());
        assertEquals("50% off", request.getOfferDetails());
    }

    @Test
    void whenSetters_thenFieldsUpdated() {
        NotificationRequest request = new NotificationRequest();
        
        request.setRecipientEmail("new@example.com");
        request.setRecipientPhone("+9876543210");
        request.setCustomerName("Jane Doe");
        request.setNotificationType(NotificationType.ORDER_DELIVERED);
        request.setChannel(NotificationChannel.WHATSAPP);
        request.setOrderId("ORD-002");
        request.setOrderDetails("New order details");
        request.setOfferDetails("New offer");

        assertEquals("new@example.com", request.getRecipientEmail());
        assertEquals("+9876543210", request.getRecipientPhone());
        assertEquals("Jane Doe", request.getCustomerName());
        assertEquals(NotificationType.ORDER_DELIVERED, request.getNotificationType());
        assertEquals(NotificationChannel.WHATSAPP, request.getChannel());
        assertEquals("ORD-002", request.getOrderId());
        assertEquals("New order details", request.getOrderDetails());
        assertEquals("New offer", request.getOfferDetails());
    }

    @Test
    void whenBuilder_thenObjectCreatedCorrectly() {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("builder@example.com")
                .recipientPhone("+1111111111")
                .customerName("Builder User")
                .notificationType(NotificationType.ORDER_CANCELLED)
                .channel(NotificationChannel.BOTH)
                .orderId("ORD-003")
                .orderDetails("Builder order")
                .offerDetails("Builder offer")
                .build();

        assertEquals("builder@example.com", request.getRecipientEmail());
        assertEquals("+1111111111", request.getRecipientPhone());
        assertEquals("Builder User", request.getCustomerName());
        assertEquals(NotificationType.ORDER_CANCELLED, request.getNotificationType());
        assertEquals(NotificationChannel.BOTH, request.getChannel());
        assertEquals("ORD-003", request.getOrderId());
        assertEquals("Builder order", request.getOrderDetails());
        assertEquals("Builder offer", request.getOfferDetails());
    }

    @Test
    void whenEqualsAndHashCode_thenSameObjectsEqual() {
        NotificationRequest request1 = NotificationRequest.builder()
                .recipientEmail("test@example.com")
                .customerName("John Doe")
                .build();

        NotificationRequest request2 = NotificationRequest.builder()
                .recipientEmail("test@example.com")
                .customerName("John Doe")
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void whenToString_thenReturnsString() {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("test@example.com")
                .customerName("John Doe")
                .build();

        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("John Doe"));
    }
}
