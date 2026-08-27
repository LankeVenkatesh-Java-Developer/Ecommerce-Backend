package com.venkatesh.it.usermanagementservice.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    // Database configuration is handled in application.properties
    // JPA auditing is handled via @CreationTimestamp and @UpdateTimestamp annotations in entities
}
