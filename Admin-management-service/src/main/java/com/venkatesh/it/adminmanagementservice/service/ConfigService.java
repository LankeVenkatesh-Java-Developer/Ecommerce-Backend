package com.venkatesh.it.adminmanagementservice.service;

import com.venkatesh.it.adminmanagementservice.dto.ConfigDTO;

import java.util.List;
import java.util.Map;

public interface ConfigService {
    List<ConfigDTO> getAllConfigs();
    Map<String, String> getServiceConfigs(String serviceName);
    void updateServiceConfig(String serviceName, Map<String, String> configs);
    Map<String, String> getNotificationConfig();
    void updateNotificationConfig(Map<String, String> config);
    boolean testNotification(Map<String, String> testData);
}
