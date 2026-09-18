package com.venkatesh.it.adminmanagementservice.controller;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;
import com.venkatesh.it.adminmanagementservice.service.ConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    private ConfigDTO testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new ConfigDTO("user-service", "jwt.secret", "test-secret", "JWT secret key", true);
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetAllConfigs_thenReturnConfigs() throws Exception {
        when(configService.getAllConfigs()).thenReturn(List.of(testConfig));

        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("user-service"));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetServiceConfigs_thenReturnConfigs() throws Exception {
        Map<String, String> configs = new HashMap<>();
        configs.put("jwt.secret", "test-secret");
        when(configService.getServiceConfigs(anyString())).thenReturn(configs);

        mockMvc.perform(get("/config/service/user-service"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenUpdateServiceConfig_thenReturnOk() throws Exception {
        Map<String, String> configs = new HashMap<>();
        configs.put("jwt.secret", "new-secret");

        mockMvc.perform(put("/config/service/user-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jwt.secret\":\"new-secret\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGetNotificationConfig_thenReturnConfig() throws Exception {
        Map<String, String> config = new HashMap<>();
        when(configService.getNotificationConfig()).thenReturn(config);

        mockMvc.perform(get("/config/notification"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenUpdateNotificationConfig_thenReturnOk() throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put("email.enabled", "true");

        mockMvc.perform(put("/config/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email.enabled\":\"true\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenTestNotification_thenReturnOk() throws Exception {
        when(configService.testNotification(any())).thenReturn(true);

        mockMvc.perform(post("/config/test-notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk());
    }
}
