package com.venkatesh.it.notificationmanagementservice.controller;

import com.venkatesh.it.notificationmanagementservice.model.NotificationChannel;
import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationResponse;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import com.venkatesh.it.notificationmanagementservice.service.EmailService;
import com.venkatesh.it.notificationmanagementservice.service.WhatsAppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private WhatsAppService whatsAppService;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = NotificationRequest.builder()
                .recipientEmail("test@example.com")
                .recipientPhone("+1234567890")
                .customerName("John Doe")
                .orderId("ORD-001")
                .orderDetails("Test order details")
                .notificationType(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.EMAIL)
                .build();
    }

    @Test
    void whenSendNotificationEmail_thenReturnSuccess() throws Exception {
        when(emailService.sendOrderCreationEmail(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));
    }

    @Test
    void whenSendNotificationWhatsApp_thenReturnSuccess() throws Exception {
        notificationRequest.setChannel(NotificationChannel.WHATSAPP);
        when(whatsAppService.sendOrderCreationWhatsApp(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.whatsappStatus").value("Sent"));
    }

    @Test
    void whenSendNotificationBoth_thenReturnSuccess() throws Exception {
        notificationRequest.setChannel(NotificationChannel.BOTH);
        when(emailService.sendOrderCreationEmail(any(NotificationRequest.class))).thenReturn(true);
        when(whatsAppService.sendOrderCreationWhatsApp(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"))
                .andExpect(jsonPath("$.whatsappStatus").value("Sent"));
    }

    @Test
    void whenOrderCreated_thenReturnSuccess() throws Exception {
        when(emailService.sendOrderCreationEmail(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/order-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void whenOrderDelivered_thenReturnSuccess() throws Exception {
        when(emailService.sendOrderDeliveryEmail(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/order-delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void whenOrderCancelled_thenReturnSuccess() throws Exception {
        when(emailService.sendOrderCancellationEmail(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/order-cancelled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void whenOfferUpdate_thenReturnSuccess() throws Exception {
        notificationRequest.setNotificationType(NotificationType.OFFER_UPDATE);
        notificationRequest.setOfferDetails("50% off on all items");
        when(emailService.sendOfferUpdateEmail(any(NotificationRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/notifications/offer-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
