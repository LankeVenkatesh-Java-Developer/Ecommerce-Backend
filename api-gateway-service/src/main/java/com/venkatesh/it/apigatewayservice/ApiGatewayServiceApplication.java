package com.venkatesh.it.apigatewayservice;

import com.venkatesh.it.apigatewayservice.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayServiceApplication {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${products.service.url}")
    private String productsServiceUrl;

    @Value("${admin.service.url}")
    private String adminServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public ApiGatewayServiceApplication(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/v1/**")
                        .and()
                        .not(p -> p.path("/api/v1/orders/**")) // Exclude orders
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(userServiceUrl))

                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(orderServiceUrl))

                // Admin Service Routes
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(adminServiceUrl))

                // Products Service Routes
                .route("products-service", r -> r
                        .path("/api/**")
                        .and()
                        .not(p -> p.path("/api/v1/**"))
                        .and()
                        .not(p -> p.path("/api/admin/**"))
                        .uri(productsServiceUrl))

                // Cart Service Routes
                .route("cart-service", r -> r
                        .path("/cart/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(cartServiceUrl))

                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri(notificationServiceUrl))

                .build();
    }
}
