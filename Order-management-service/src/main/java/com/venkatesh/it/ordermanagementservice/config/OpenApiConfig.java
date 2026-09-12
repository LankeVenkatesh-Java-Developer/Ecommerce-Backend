package com.venkatesh.it.ordermanagementservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8084}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");
        
        Contact contact = new Contact();
        contact.setEmail("support@ecommerce.com");
        contact.setName("Order Service Support");
        
        Info info = new Info()
                .title("Order Service API")
                .version("1.0")
                .description("Order Management Microservice API - Handles order creation, payment gateway integration, order updates, order cancellation, and retrieving orders based on customer information")
                .contact(contact);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
