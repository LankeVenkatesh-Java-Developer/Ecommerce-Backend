package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}