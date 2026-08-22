package com.ashok.it.userservice.Config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
}
