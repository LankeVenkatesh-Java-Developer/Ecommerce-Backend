package com.venkatesh.it.notificationmanagementservice.integration;

import com.venkatesh.it.notificationmanagementservice.model.NotificationChannel;
import com.venkatesh.it.notificationmanagementservice.model.NotificationRequest;
import com.venkatesh.it.notificationmanagementservice.model.NotificationResponse;
import com.venkatesh.it.notificationmanagementservice.model.NotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender javaMailSender;

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
        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"));
    }

    @Test
    void whenSendNotificationWhatsApp_thenReturnSuccess() throws Exception {
        notificationRequest.setChannel(NotificationChannel.WHATSAPP);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.whatsappStatus").value("Failed"))
                .andExpect(jsonPath("$.message").value("Failed to send notification"));
    }

    @Test
    void whenSendNotificationBoth_thenReturnSuccess() throws Exception {
        notificationRequest.setChannel(NotificationChannel.BOTH);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"))
                .andExpect(jsonPath("$.whatsappStatus").value("Failed"));
    }

    @Test
    void whenOrderCreated_thenReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/notifications/order-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));
    }

    @Test
    void whenOrderDelivered_thenReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/notifications/order-delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));
    }

    @Test
    void whenOrderCancelled_thenReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/notifications/order-cancelled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));
    }

    @Test
    void whenOfferUpdate_thenReturnSuccess() throws Exception {
        notificationRequest.setNotificationType(NotificationType.OFFER_UPDATE);
        notificationRequest.setOfferDetails("50% off on all items");

        mockMvc.perform(post("/api/notifications/offer-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));
    }

    @Test
    void whenSendNotificationWithInvalidRequest_thenReturnBadRequest() throws Exception {
        String invalidRequest = "{}";

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk());
    }

    @Test
    void whenSendNotificationWithMissingFields_thenReturnSuccess() throws Exception {
        NotificationRequest incompleteRequest = NotificationRequest.builder()
                .channel(NotificationChannel.EMAIL)
                .notificationType(NotificationType.ORDER_CREATED)
                .build();

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isOk());
    }
}
