package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Received login request for email: {}", request.getEmail());
        return authService.login(request)
                .doOnSuccess(response -> log.info("Login successful for email: {}", request.getEmail()))
                .doOnError(error -> log.error("Login failed for email: {}", request.getEmail(), error));
    }
}