package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.UserRepository;
import com.ashok.it.userservice.Service.AuthService;
import com.ashok.it.userservice.Service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "Invalid email or password"
                )))
                .flatMap(user -> {
                    log.info("User found with ID: {}, Status: {}", user.getId(), user.getStatus());

                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        log.warn("Password mismatch for email: {}", request.getEmail());
                        return Mono.error(new BadCredentialsException(
                                "Invalid email or password"
                        ));
                    }

                    if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        log.warn("User account not active for email: {}, status: {}", request.getEmail(), user.getStatus());
                        return Mono.error(new BadCredentialsException(
                                "User account is not active"
                        ));
                    }

                    try {
                        String token = jwtService.generateToken(user.getId(), user.getEmail());
                        log.info("JWT token generated successfully for user ID: {}", user.getId());

                        LoginResponse response = new LoginResponse();
                        response.setUserId(user.getId());
                        response.setEmail(user.getEmail());
                        response.setAccessToken(token);
                        response.setTokenType("Bearer");
                        response.setExpiresIn(3600L);

                        return Mono.just(response);
                    } catch (Exception e) {
                        log.error("Failed to generate JWT token for user ID: {}", user.getId(), e);
                        return Mono.error(new RuntimeException("Failed to generate authentication token", e));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Login failed for email: {}", request.getEmail(), e);
                    return Mono.error(e);
                });
    }
}