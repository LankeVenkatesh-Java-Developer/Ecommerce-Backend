package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.RegisterRequest;
import com.ashok.it.userservice.Dto.UserResponse;
import com.ashok.it.userservice.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request,
            ServerWebExchange exchange) {

        return userService.registerUser(request)
                .map(response -> {
                    exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                    return response;
                });
    }

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUser(
            @PathVariable Long userId) {

        return userService.getUserById(userId);
    }
}