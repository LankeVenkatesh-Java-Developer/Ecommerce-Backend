package com.venkatesh.it.adminmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDTO {
    private String serviceName;
    private String configKey;
    private String configValue;
    private String description;
    private boolean isSensitive;
}
