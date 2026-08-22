package com.ashok.it.userservice.Service;

import com.ashok.it.userservice.Dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * Get user's order history from OrderService
     * Uses service discovery (Eureka) to resolve OrderService
     */
    public Flux<OrderResponse> getUserOrders(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/user/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToFlux(OrderResponse.class)
                .doOnError(error -> log.error("Error fetching orders for user {}: {}", userId, error.getMessage()));
    }

    /**
     * Get specific order details from OrderService
     */
    public Mono<OrderResponse> getOrderById(Long orderId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/{orderId}")
                        .build(orderId))
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .doOnError(error -> log.error("Error fetching order {}: {}", orderId, error.getMessage()));
    }

    /**
     * Check if user has active orders before allowing account deletion
     */
    public Mono<Boolean> hasActiveOrders(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/user/{userId}/active")
                        .build(userId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false)
                .doOnError(error -> log.error("Error checking active orders for user {}: {}", userId, error.getMessage()));
    }
}
