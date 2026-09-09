package com.venkatesh.it.adminmanagementservice.config;

import com.venkatesh.it.adminmanagementservice.security.JwtAuthenticationFilter;
import com.venkatesh.it.adminmanagementservice.security.JwtService;
import com.venkatesh.it.adminmanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtService jwtService;
    private final UserService userService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userService);
    }
}
