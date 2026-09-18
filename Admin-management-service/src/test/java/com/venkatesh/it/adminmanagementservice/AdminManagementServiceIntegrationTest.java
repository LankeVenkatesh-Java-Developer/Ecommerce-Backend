package com.venkatesh.it.adminmanagementservice;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;
import com.venkatesh.it.adminmanagementservice.service.ConfigService;
import com.venkatesh.it.adminmanagementservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AdminManagementServiceIntegrationTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertNotNull(configService);
        assertNotNull(userService);
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetAllConfigs_thenReturnConfigList() {
        var configs = configService.getAllConfigs();

        assertNotNull(configs);
        assertFalse(configs.isEmpty());
        assertTrue(configs.stream().anyMatch(c -> c.getServiceName().equals("user-service")));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetServiceConfigs_thenReturnConfigs() {
        Map<String, String> configs = configService.getServiceConfigs("user-service");

        assertNotNull(configs);
        assertEquals("8081", configs.get("port"));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetNotificationConfig_thenReturnConfig() {
        Map<String, String> config = configService.getNotificationConfig();

        assertNotNull(config);
        assertEquals("smtp.gmail.com", config.get("mail.host"));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenUpdateServiceConfig_thenCompleteSuccessfully() {
        Map<String, String> configs = Map.of("port", "9090");

        assertDoesNotThrow(() -> configService.updateServiceConfig("user-service", configs));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenUpdateNotificationConfig_thenCompleteSuccessfully() {
        Map<String, String> config = Map.of("mail.host", "smtp.example.com");

        assertDoesNotThrow(() -> configService.updateNotificationConfig(config));
    }

    @Test
    void whenGetUserRoles_thenReturnRoles() {
        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void whenIsValidUser_thenReturnTrue() {
        boolean isValid = userService.isValidUser(1L);

        assertTrue(isValid);
    }
}
