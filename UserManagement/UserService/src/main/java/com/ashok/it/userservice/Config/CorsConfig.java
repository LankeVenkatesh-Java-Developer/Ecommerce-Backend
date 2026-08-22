package com.ashok.it.userservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow specific origins including Vercel frontend and local development
        corsConfig.setAllowedOrigins(Arrays.asList(
            "https://ecommerce-front-end-eta.vercel.app",
            "http://localhost:5173",
            "http://localhost:3000",
            "*"
        ));
        
        // Allow common HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow common headers
        corsConfig.setAllowedHeaders(List.of("*"));
        
        // Allow credentials
        corsConfig.setAllowCredentials(true);
        
        // Set max age for preflight requests
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
