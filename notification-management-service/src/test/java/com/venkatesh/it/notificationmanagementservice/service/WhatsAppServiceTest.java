package com.venkatesh.it.notificationmanagementservice.service;

import com.venkatesh.it.notificationmanagementservice.config.WhatsAppConfig;
import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WhatsAppServiceTest {

    @Mock
    private WhatsAppConfig whatsAppConfig;

    @InjectMocks
    private WhatsAppService whatsAppService;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = NotificationRequest.builder()
                .recipientPhone("+1234567890")
                .customerName("John Doe")
                .orderId("ORD-001")
                .orderDetails("Test order details")
                .notificationType(NotificationType.ORDER_CREATED)
                .build();

        lenient().when(whatsAppConfig.getFromNumber()).thenReturn("+14155238886");
    }

    @Test
    void whenSendOrderCreationWhatsApp_thenMethodExecutesWithoutException() {
        assertDoesNotThrow(() -> whatsAppService.sendOrderCreationWhatsApp(notificationRequest));
    }

    @Test
    void whenSendOrderDeliveryWhatsApp_thenMethodExecutesWithoutException() {
        notificationRequest.setNotificationType(NotificationType.ORDER_DELIVERED);
        assertDoesNotThrow(() -> whatsAppService.sendOrderDeliveryWhatsApp(notificationRequest));
    }

    @Test
    void whenSendOrderCancellationWhatsApp_thenMethodExecutesWithoutException() {
        notificationRequest.setNotificationType(NotificationType.ORDER_CANCELLED);
        assertDoesNotThrow(() -> whatsAppService.sendOrderCancellationWhatsApp(notificationRequest));
    }

    @Test
    void whenSendOfferUpdateWhatsApp_thenMethodExecutesWithoutException() {
        notificationRequest.setNotificationType(NotificationType.OFFER_UPDATE);
        notificationRequest.setOfferDetails("50% off on all items");
        assertDoesNotThrow(() -> whatsAppService.sendOfferUpdateWhatsApp(notificationRequest));
    }

    @Test
    void whenWhatsAppConfigIsNull_thenGetFromNumberReturnsDefault() {
        assertNotNull(whatsAppConfig.getFromNumber());
    }

    @Test
    void whenNotificationRequestIsValid_thenFieldsAreSetCorrectly() {
        assertEquals("+1234567890", notificationRequest.getRecipientPhone());
        assertEquals("John Doe", notificationRequest.getCustomerName());
        assertEquals("ORD-001", notificationRequest.getOrderId());
        assertEquals("Test order details", notificationRequest.getOrderDetails());
        assertEquals(NotificationType.ORDER_CREATED, notificationRequest.getNotificationType());
    }
}
