package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.UserRepository;
import com.ashok.it.userservice.Service.AuthService;
import com.ashok.it.userservice.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "Invalid email or password"
                )))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new BadCredentialsException(
                                "Invalid email or password"
                        ));
                    }

                    if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        return Mono.error(new BadCredentialsException(
                                "User account is not active"
                        ));
                    }

                    String token = jwtService.generateToken(user.getId(), user.getEmail());

                    LoginResponse response = new LoginResponse();
                    response.setUserId(user.getId());
                    response.setEmail(user.getEmail());
                    response.setAccessToken(token);
                    response.setTokenType("Bearer");
                    response.setExpiresIn(3600L);

                    return Mono.just(response);
                });
    }
}