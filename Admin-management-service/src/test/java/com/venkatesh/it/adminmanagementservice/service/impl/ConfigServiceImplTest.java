package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ConfigServiceImpl configService;

    @Test
    void whenGetAllConfigs_thenReturnConfigList() {
        List<ConfigDTO> configs = configService.getAllConfigs();

        assertNotNull(configs);
        assertFalse(configs.isEmpty());
        assertTrue(configs.stream().anyMatch(c -> c.getServiceName().equals("user-service")));
        assertTrue(configs.stream().anyMatch(c -> c.getServiceName().equals("products-service")));
    }

    @Test
    void whenGetServiceConfigsForUserService_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("user-service");

        assertNotNull(configs);
        assertEquals("8081", configs.get("port"));
        assertTrue(configs.containsKey("database.url"));
        assertTrue(configs.containsKey("jwt.expiration"));
    }

    @Test
    void whenGetServiceConfigsForProductsService_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("products-service");

        assertNotNull(configs);
        assertEquals("8082", configs.get("port"));
        assertTrue(configs.containsKey("database.url"));
    }

    @Test
    void whenGetServiceConfigsForAdminService_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("admin-service");

        assertNotNull(configs);
        assertEquals("8083", configs.get("port"));
    }

    @Test
    void whenGetServiceConfigsForOrderService_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("order-service");

        assertNotNull(configs);
        assertEquals("8084", configs.get("port"));
        assertEquals("test", configs.get("razorpay.mode"));
    }

    @Test
    void whenGetServiceConfigsForNotificationService_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("notification-service");

        assertNotNull(configs);
        assertEquals("8085", configs.get("port"));
        assertEquals("smtp.gmail.com", configs.get("mail.host"));
    }

    @Test
    void whenGetServiceConfigsForUnknownService_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            configService.getServiceConfigs("unknown-service");
        });
    }

    @Test
    void whenUpdateServiceConfig_thenLogUpdate() {
        Map<String, String> configs = Map.of("port", "9090", "jwt.secret", "new-secret");

        assertDoesNotThrow(() -> configService.updateServiceConfig("user-service", configs));
    }

    @Test
    void whenGetNotificationConfig_thenReturnConfig() {
        Map<String, String> config = configService.getNotificationConfig();

        assertNotNull(config);
        assertEquals("smtp.gmail.com", config.get("mail.host"));
        assertEquals("587", config.get("mail.port"));
        assertTrue(config.containsKey("mail.username"));
        assertTrue(config.containsKey("twilio.account.sid"));
    }

    @Test
    void whenUpdateNotificationConfig_thenLogUpdate() {
        Map<String, String> config = Map.of("mail.host", "smtp.example.com", "mail.port", "25");

        assertDoesNotThrow(() -> configService.updateNotificationConfig(config));
    }

    @Test
    void whenTestNotification_thenReturnTrue() {
        when(restTemplate.postForObject(any(String.class), any(), eq(Map.class)))
                .thenReturn(Map.of("success", true));

        Map<String, String> testData = Map.of("email", "test@example.com", "phone", "+1234567890");

        boolean result = configService.testNotification(testData);

        assertTrue(result);
        verify(restTemplate, times(1)).postForObject(any(String.class), any(), eq(Map.class));
    }

    @Test
    void whenTestNotificationFails_thenReturnFalse() {
        when(restTemplate.postForObject(any(String.class), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Map<String, String> testData = Map.of("email", "test@example.com");

        boolean result = configService.testNotification(testData);

        assertFalse(result);
    }
}
