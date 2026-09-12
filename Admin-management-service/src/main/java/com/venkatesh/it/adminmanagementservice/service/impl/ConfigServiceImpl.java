package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;
import com.venkatesh.it.adminmanagementservice.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.base-url:http://localhost:8085}")
    private String notificationServiceUrl;

    public ConfigServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ConfigDTO> getAllConfigs() {
        List<ConfigDTO> configs = new ArrayList<>();
        
        // User Service Configs
        configs.add(new ConfigDTO("user-service", "port", "8081", "User service port", false));
        configs.add(new ConfigDTO("user-service", "jwt.secret", "***", "JWT secret key", true));
        
        // Products Service Configs
        configs.add(new ConfigDTO("products-service", "port", "8082", "Products service port", false));
        configs.add(new ConfigDTO("products-service", "jwt.secret", "***", "JWT secret key", true));
        
        // Admin Service Configs
        configs.add(new ConfigDTO("admin-service", "port", "8083", "Admin service port", false));
        configs.add(new ConfigDTO("admin-service", "jwt.secret", "***", "JWT secret key", true));
        
        // Order Service Configs
        configs.add(new ConfigDTO("order-service", "port", "8084", "Order service port", false));
        configs.add(new ConfigDTO("order-service", "razorpay.key.id", "***", "Razorpay key ID", true));
        
        // Notification Service Configs
        configs.add(new ConfigDTO("notification-service", "port", "8085", "Notification service port", false));
        configs.add(new ConfigDTO("notification-service", "mail.host", "smtp.gmail.com", "Email SMTP host", false));
        configs.add(new ConfigDTO("notification-service", "mail.username", "***", "Email username", true));
        configs.add(new ConfigDTO("notification-service", "twilio.account.sid", "***", "Twilio account SID", true));
        
        return configs;
    }

    @Override
    public Map<String, String> getServiceConfigs(String serviceName) {
        Map<String, String> configs = new HashMap<>();
        
        switch (serviceName.toLowerCase()) {
            case "user-service":
                configs.put("port", "8081");
                configs.put("database.url", "jdbc:mysql://localhost:3306/user_management_db");
                configs.put("jwt.expiration", "86400000");
                break;
            case "products-service":
                configs.put("port", "8082");
                configs.put("database.url", "jdbc:mysql://localhost:3306/products_management_db");
                configs.put("jwt.expiration", "86400000");
                break;
            case "admin-service":
                configs.put("port", "8083");
                configs.put("database.url", "jdbc:mysql://localhost:3306/admin_management_db");
                configs.put("jwt.expiration", "86400000");
                break;
            case "order-service":
                configs.put("port", "8084");
                configs.put("database.url", "jdbc:mysql://localhost:3306/order_service_db");
                configs.put("razorpay.mode", "test");
                break;
            case "notification-service":
                configs.put("port", "8085");
                configs.put("mail.host", "smtp.gmail.com");
                configs.put("mail.port", "587");
                configs.put("whatsapp.from.number", "+14155238886");
                break;
            default:
                throw new IllegalArgumentException("Unknown service: " + serviceName);
        }
        
        return configs;
    }

    @Override
    public void updateServiceConfig(String serviceName, Map<String, String> configs) {
        // In a real implementation, this would update a configuration store
        // For now, we'll log the update
        System.out.println("Updating configuration for service: " + serviceName);
        configs.forEach((key, value) -> 
            System.out.println("  " + key + " = " + (isSensitiveKey(key) ? "***" : value))
        );
        
        // TODO: Implement actual configuration persistence
        // This could update environment variables, a config server, or database
    }

    @Override
    public Map<String, String> getNotificationConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("mail.host", "smtp.gmail.com");
        config.put("mail.port", "587");
        config.put("mail.username", "***");
        config.put("mail.password", "***");
        config.put("twilio.account.sid", "***");
        config.put("twilio.auth.token", "***");
        config.put("twilio.whatsapp.from.number", "+14155238886");
        return config;
    }

    @Override
    public void updateNotificationConfig(Map<String, String> config) {
        System.out.println("Updating notification configuration:");
        config.forEach((key, value) -> 
            System.out.println("  " + key + " = " + (isSensitiveKey(key) ? "***" : value))
        );
        
        // TODO: Implement actual configuration update
        // This would update the notification service's configuration
    }

    @Override
    public boolean testNotification(Map<String, String> testData) {
        try {
            String url = notificationServiceUrl + "/api/notifications/send";
            Map<String, Object> request = new HashMap<>();
            request.put("recipientEmail", testData.get("email"));
            request.put("recipientPhone", testData.get("phone"));
            request.put("customerName", "Test User");
            request.put("notificationType", "OFFER_UPDATE");
            request.put("channel", testData.getOrDefault("channel", "EMAIL"));
            request.put("orderId", "TEST-001");
            request.put("orderDetails", "Test order");
            request.put("offerDetails", "This is a test notification from admin panel");
            
            restTemplate.postForObject(url, request, Map.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isSensitiveKey(String key) {
        return key.toLowerCase().contains("password") || 
               key.toLowerCase().contains("secret") || 
               key.toLowerCase().contains("token") ||
               key.toLowerCase().contains("key");
    }
}
