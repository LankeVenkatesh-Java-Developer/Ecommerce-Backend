package com.ashok.it.userservice.Service;

import com.ashok.it.userservice.Dto.NotificationRequest;
import com.ashok.it.userservice.Dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * Send notification via NotificationService
     * Uses service discovery (Eureka) to resolve NotificationService
     */
    public Mono<NotificationResponse> sendNotification(NotificationRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("/api/v1/notifications/send")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotificationResponse.class)
                .doOnError(error -> log.error("Error sending notification: {}", error.getMessage()));
    }

    /**
     * Send welcome email to new user
     */
    public Mono<NotificationResponse> sendWelcomeEmail(String email, Long userId) {
        NotificationRequest request = new NotificationRequest();
        request.setType("WELCOME_EMAIL");
        request.setRecipient(email);
        request.setUserId(userId);
        request.setSubject("Welcome to Our E-commerce Platform!");
        request.setMessage("Thank you for registering with us. Your account has been successfully created.");

        return sendNotification(request);
    }

    /**
     * Send password reset email
     */
    public Mono<NotificationResponse> sendPasswordResetEmail(String email, String resetToken) {
        NotificationRequest request = new NotificationRequest();
        request.setType("PASSWORD_RESET");
        request.setRecipient(email);
        request.setSubject("Password Reset Request");
        request.setMessage("Use this token to reset your password: " + resetToken);

        return sendNotification(request);
    }
}
