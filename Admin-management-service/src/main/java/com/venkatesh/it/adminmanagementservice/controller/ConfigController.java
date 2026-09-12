package com.venkatesh.it.adminmanagementservice.controller;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;
import com.venkatesh.it.adminmanagementservice.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Tag(name = "Configuration Management", description = "APIs for managing application configurations")
@SecurityRequirement(name = "bearerAuth")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    @Operation(summary = "Get all configurations", description = "Retrieve all application configurations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ConfigDTO>> getAllConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    @GetMapping("/service/{serviceName}")
    @Operation(summary = "Get service configurations", description = "Retrieve configurations for a specific service")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> getServiceConfigs(@PathVariable String serviceName) {
        return ResponseEntity.ok(configService.getServiceConfigs(serviceName));
    }

    @PutMapping("/service/{serviceName}")
    @Operation(summary = "Update service configuration", description = "Update configuration for a specific service")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> updateServiceConfig(
            @PathVariable String serviceName,
            @RequestBody Map<String, String> configs) {
        configService.updateServiceConfig(serviceName, configs);
        return ResponseEntity.ok("Configuration updated successfully");
    }

    @GetMapping("/notification")
    @Operation(summary = "Get notification service configuration", description = "Retrieve notification service settings")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> getNotificationConfig() {
        return ResponseEntity.ok(configService.getNotificationConfig());
    }

    @PutMapping("/notification")
    @Operation(summary = "Update notification configuration", description = "Update email and WhatsApp settings")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> updateNotificationConfig(@RequestBody Map<String, String> config) {
        configService.updateNotificationConfig(config);
        return ResponseEntity.ok("Notification configuration updated successfully");
    }

    @PostMapping("/test-notification")
    @Operation(summary = "Test notification service", description = "Send a test notification")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> testNotification(@RequestBody Map<String, String> testData) {
        boolean success = configService.testNotification(testData);
        return ResponseEntity.ok(success ? "Test notification sent successfully" : "Failed to send test notification");
    }
}
