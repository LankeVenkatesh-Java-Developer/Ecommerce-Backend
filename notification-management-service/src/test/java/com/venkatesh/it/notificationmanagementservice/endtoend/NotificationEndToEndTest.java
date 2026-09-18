package com.venkatesh.it.notificationmanagementservice.endtoend;

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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void testCompleteOrderCreationFlow_Email() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .recipientPhone("+1234567890")
                .customerName("Jane Smith")
                .orderId("ORD-2024-001")
                .orderDetails("2x Product A, 1x Product B - Total: $99.99")
                .notificationType(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.EMAIL)
                .build();

        MvcResult result = mockMvc.perform(post("/api/notifications/order-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        NotificationResponse response = objectMapper.readValue(responseContent, NotificationResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("Sent", response.getEmailStatus());
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testCompleteOrderDeliveryFlow_BothChannels() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .recipientPhone("+1234567890")
                .customerName("Jane Smith")
                .orderId("ORD-2024-001")
                .orderDetails("2x Product A, 1x Product B - Total: $99.99")
                .notificationType(NotificationType.ORDER_DELIVERED)
                .channel(NotificationChannel.BOTH)
                .build();

        mockMvc.perform(post("/api/notifications/order-delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"))
                .andExpect(jsonPath("$.whatsappStatus").value("Failed"));

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testCompleteOrderCancellationFlow_WhatsApp() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .recipientPhone("+1234567890")
                .customerName("Jane Smith")
                .orderId("ORD-2024-001")
                .orderDetails("2x Product A, 1x Product B - Total: $99.99")
                .notificationType(NotificationType.ORDER_CANCELLED)
                .channel(NotificationChannel.WHATSAPP)
                .build();

        mockMvc.perform(post("/api/notifications/order-cancelled")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.whatsappStatus").value("Failed"));
    }

    @Test
    void testCompleteOfferUpdateFlow_Email() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .recipientPhone("+1234567890")
                .customerName("Jane Smith")
                .notificationType(NotificationType.OFFER_UPDATE)
                .channel(NotificationChannel.EMAIL)
                .offerDetails("Flash Sale: 50% off on all electronics! Valid until midnight.")
                .build();

        mockMvc.perform(post("/api/notifications/offer-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.emailStatus").value("Sent"));

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testMultipleNotificationsInSequence() throws Exception {
        NotificationRequest orderRequest = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .customerName("Jane Smith")
                .orderId("ORD-2024-002")
                .orderDetails("1x Product C - Total: $49.99")
                .notificationType(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.EMAIL)
                .build();

        mockMvc.perform(post("/api/notifications/order-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        orderRequest.setNotificationType(NotificationType.ORDER_DELIVERED);

        mockMvc.perform(post("/api/notifications/order-delivered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testNotificationFailureScenario() throws Exception {
        doThrow(new RuntimeException("SMTP server unavailable"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .customerName("Jane Smith")
                .orderId("ORD-2024-003")
                .orderDetails("1x Product D - Total: $29.99")
                .notificationType(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.EMAIL)
                .build();

        MvcResult result = mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        NotificationResponse response = objectMapper.readValue(responseContent, NotificationResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Failed", response.getEmailStatus());
        assertEquals("Failed to send notification", response.getMessage());
    }

    @Test
    void testNotificationWithAllNotificationTypes() throws Exception {
        NotificationRequest baseRequest = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .customerName("Test Customer")
                .orderId("ORD-TEST-001")
                .orderDetails("Test order")
                .offerDetails("Test offer")
                .channel(NotificationChannel.EMAIL)
                .build();

        for (NotificationType type : NotificationType.values()) {
            baseRequest.setNotificationType(type);
            
            mockMvc.perform(post("/api/notifications/send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(baseRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.emailStatus").value("Sent"));
        }

        verify(javaMailSender, times(4)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testNotificationWithAllChannels() throws Exception {
        NotificationRequest baseRequest = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .recipientPhone("+1234567890")
                .customerName("Test Customer")
                .orderId("ORD-TEST-002")
                .orderDetails("Test order")
                .notificationType(NotificationType.ORDER_CREATED)
                .build();

        for (NotificationChannel channel : NotificationChannel.values()) {
            baseRequest.setChannel(channel);
            
            if (channel == NotificationChannel.WHATSAPP) {
                mockMvc.perform(post("/api/notifications/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(baseRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(false));
            } else {
                mockMvc.perform(post("/api/notifications/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(baseRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }
        }
    }

    @Test
    void testNotificationResponseStructure() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipientEmail("customer@example.com")
                .customerName("Test Customer")
                .orderId("ORD-TEST-003")
                .orderDetails("Test order")
                .notificationType(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.BOTH)
                .build();

        MvcResult result = mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        NotificationResponse response = objectMapper.readValue(responseContent, NotificationResponse.class);

        assertNotNull(response);
        assertNotNull(response.isSuccess());
        assertNotNull(response.getMessage());
        assertNotNull(response.getEmailStatus());
        assertNotNull(response.getWhatsappStatus());
    }
}
