package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.RegisterRequest;
import com.ashok.it.userservice.Dto.UserResponse;
import com.ashok.it.userservice.Exception.DuplicateUserException;
import com.ashok.it.userservice.Exception.InvalidPasswordException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Service.UserService;
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
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests (Simplified)")
class UserControllerSimpleTest {

    @Mock
    private UserService userService;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Mock
    private org.springframework.http.server.reactive.ServerHttpResponse serverHttpResponse;

    @InjectMocks
    private UserController userController;

    private RegisterRequest registerRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        when(serverWebExchange.getResponse()).thenReturn(serverHttpResponse);
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setMobileNumber("9876543210");
        registerRequest.setPassword("SecurePass123");
        registerRequest.setConfirmPassword("SecurePass123");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setMobileNumber("9876543210");
        userResponse.setStatus("ACTIVE");
    }

    @Nested
    @DisplayName("registerUser() Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully with valid request")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(userService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.just(userResponse));

            // Act
            UserResponse response = userController.registerUser(registerRequest, serverWebExchange).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailExists() {
            // Arrange
            when(userService.registerUser(any(RegisterRequest.class)))
                    .thenReturn(Mono.error(new DuplicateUserException("Email already registered")));

            // Act & Assert
            try {
                userController.registerUser(registerRequest, serverWebExchange).block();
            } catch (DuplicateUserException e) {
                assertThat(e.getMessage()).isEqualTo("Email already registered");
            }
        }

        @Test
        @DisplayName("Should return 400 when passwords do not match")
        void shouldReturn400WhenPasswordsDoNotMatch() {
            // Arrange
            when(userService.registerUser(any(RegisterRequest.class)))
                    .thenReturn(Mono.error(new InvalidPasswordException("Password and confirm password do not match")));

            // Act & Assert
            try {
                userController.registerUser(registerRequest, serverWebExchange).block();
            } catch (InvalidPasswordException e) {
                assertThat(e.getMessage()).isEqualTo("Password and confirm password do not match");
            }
        }
    }

    @Nested
    @DisplayName("getUser() Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return user when user exists")
        void shouldReturnUserWhenExists() {
            // Arrange
            Long userId = 1L;
            when(userService.getUserById(userId)).thenReturn(Mono.just(userResponse));

            // Act
            UserResponse response = userController.getUser(userId).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            Long userId = 999L;
            when(userService.getUserById(userId))
                    .thenReturn(Mono.error(new UserNotFoundException("User not found with id: " + userId)));

            // Act & Assert
            try {
                userController.getUser(userId).block();
            } catch (UserNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("User not found with id: " + userId);
            }
        }
    }
}
