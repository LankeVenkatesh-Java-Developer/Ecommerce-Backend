package com.ashok.it.userservice.Config;

import org.springframework.context.annotation.Configuration;

// CORS is now configured directly in SecurityConfig for WebFlux
// This separate CorsWebFilter was causing conflicts with SecurityConfig CORS
@Configuration
public class CorsConfig {
    // Disabled - CORS is now handled in SecurityConfig
}
