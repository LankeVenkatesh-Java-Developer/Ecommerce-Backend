package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests (Simplified)")
class AuthControllerSimpleTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("SecurePass123");

        loginResponse = new LoginResponse();
        loginResponse.setUserId(1L);
        loginResponse.setEmail("john.doe@example.com");
        loginResponse.setAccessToken("jwt.token.here");
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(3600L);
    }

    @Nested
    @DisplayName("login() Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            // Arrange
            when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(loginResponse));

            // Act
            LoginResponse response = authController.login(loginRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getExpiresIn()).isEqualTo(3600L);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(Mono.error(new UserNotFoundException("Invalid email or password")));

            // Act & Assert
            try {
                authController.login(loginRequest).block();
            } catch (UserNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Invalid email or password");
            }
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when credentials are invalid")
        void shouldThrowExceptionWhenCredentialsInvalid() {
            // Arrange
            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(Mono.error(new BadCredentialsException("Invalid email or password")));

            // Act & Assert
            try {
                authController.login(loginRequest).block();
            } catch (BadCredentialsException e) {
                assertThat(e.getMessage()).isEqualTo("Invalid email or password");
            }
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user account is not active")
        void shouldThrowExceptionWhenUserNotActive() {
            // Arrange
            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(Mono.error(new BadCredentialsException("User account is not active")));

            // Act & Assert
            try {
                authController.login(loginRequest).block();
            } catch (BadCredentialsException e) {
                assertThat(e.getMessage()).isEqualTo("User account is not active");
            }
        }
    }
}
