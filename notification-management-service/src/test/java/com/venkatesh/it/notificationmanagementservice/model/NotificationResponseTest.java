package com.venkatesh.it.notificationmanagementservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationResponseTest {

    @Test
    void whenNoArgsConstructor_thenObjectCreated() {
        NotificationResponse response = new NotificationResponse();
        assertNotNull(response);
    }

    @Test
    void whenAllArgsConstructor_thenAllFieldsSet() {
        NotificationResponse response = new NotificationResponse(
                true,
                "Notification sent successfully",
                "Sent",
                "Sent"
        );

        assertTrue(response.isSuccess());
        assertEquals("Notification sent successfully", response.getMessage());
        assertEquals("Sent", response.getEmailStatus());
        assertEquals("Sent", response.getWhatsappStatus());
    }

    @Test
    void whenSetters_thenFieldsUpdated() {
        NotificationResponse response = new NotificationResponse();
        
        response.setSuccess(false);
        response.setMessage("Failed to send notification");
        response.setEmailStatus("Failed");
        response.setWhatsappStatus("Failed");

        assertFalse(response.isSuccess());
        assertEquals("Failed to send notification", response.getMessage());
        assertEquals("Failed", response.getEmailStatus());
        assertEquals("Failed", response.getWhatsappStatus());
    }

    @Test
    void whenBuilder_thenObjectCreatedCorrectly() {
        NotificationResponse response = NotificationResponse.builder()
                .success(true)
                .message("Test message")
                .emailStatus("Sent")
                .whatsappStatus("Sent")
                .build();

        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Sent", response.getEmailStatus());
        assertEquals("Sent", response.getWhatsappStatus());
    }

    @Test
    void whenEqualsAndHashCode_thenSameObjectsEqual() {
        NotificationResponse response1 = NotificationResponse.builder()
                .success(true)
                .message("Test")
                .emailStatus("Sent")
                .whatsappStatus("Sent")
                .build();

        NotificationResponse response2 = NotificationResponse.builder()
                .success(true)
                .message("Test")
                .emailStatus("Sent")
                .whatsappStatus("Sent")
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void whenToString_thenReturnsString() {
        NotificationResponse response = NotificationResponse.builder()
                .success(true)
                .message("Test message")
                .build();

        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test message"));
    }

    @Test
    void whenPartialFields_thenOtherFieldsNull() {
        NotificationResponse response = new NotificationResponse();
        response.setSuccess(true);
        response.setMessage("Partial success");

        assertTrue(response.isSuccess());
        assertEquals("Partial success", response.getMessage());
        assertNull(response.getEmailStatus());
        assertNull(response.getWhatsappStatus());
    }
}
