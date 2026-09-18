package com.venkatesh.it.notificationmanagementservice.service;

import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = NotificationRequest.builder()
                .recipientEmail("test@example.com")
                .customerName("John Doe")
                .orderId("ORD-001")
                .orderDetails("Test order details")
                .notificationType(NotificationType.ORDER_CREATED)
                .build();
    }

    @Test
    void whenSendOrderCreationEmail_thenReturnTrue() {
        boolean result = emailService.sendOrderCreationEmail(notificationRequest);

        assertTrue(result);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void whenSendOrderDeliveryEmail_thenReturnTrue() {
        notificationRequest.setNotificationType(NotificationType.ORDER_DELIVERED);
        boolean result = emailService.sendOrderDeliveryEmail(notificationRequest);

        assertTrue(result);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void whenSendOrderCancellationEmail_thenReturnTrue() {
        notificationRequest.setNotificationType(NotificationType.ORDER_CANCELLED);
        boolean result = emailService.sendOrderCancellationEmail(notificationRequest);

        assertTrue(result);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void whenSendOfferUpdateEmail_thenReturnTrue() {
        notificationRequest.setNotificationType(NotificationType.OFFER_UPDATE);
        notificationRequest.setOfferDetails("50% off on all items");
        boolean result = emailService.sendOfferUpdateEmail(notificationRequest);

        assertTrue(result);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void whenSendEmailThrowsException_thenReturnFalse() {
        doThrow(new RuntimeException("SMTP error")).when(javaMailSender).send(any(SimpleMailMessage.class));

        boolean result = emailService.sendOrderCreationEmail(notificationRequest);

        assertFalse(result);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
