package com.venkatesh.it.usermanagementservice.service;

import com.venkatesh.it.usermanagementservice.exception.BadRequestException;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.dto.LoginRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginResponse;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import com.venkatesh.it.usermanagementservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserResponse register(RegisterRequest request) {
        return userService.registerUser(request);
    }

    public LoginResponse login(LoginRequest request) {
        String emailOrMobile = request.getEmailOrMobile();
        String password = request.getPassword();

        User user = userRepository.findByEmail(emailOrMobile)
                .orElseGet(() -> userRepository.findByMobileNumber(emailOrMobile)
                        .orElseThrow(() -> new BadRequestException("Invalid credentials")));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        if (user.getStatus() != com.venkatesh.it.usermanagementservice.model.enums.UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active. Status: " + user.getStatus());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrMobile, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
