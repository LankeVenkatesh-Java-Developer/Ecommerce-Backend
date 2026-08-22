package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.UserRepository;
import com.ashok.it.userservice.Service.AuthService;
import com.ashok.it.userservice.Service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("SecurePass123");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setMobileNumber("9876543210");
        user.setPassword("$2a$10$encodedPasswordHash");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("login() Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            // Arrange
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt.token.here");

            // Act
            LoginResponse response = authService.login(loginRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getExpiresIn()).isEqualTo(3600L);

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
            verify(jwtService).generateToken(user.getId(), user.getEmail());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when email not found")
        void shouldThrowExceptionWhenEmailNotFound() {
            // Arrange
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest).block())
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Invalid email or password");

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(anyLong(), anyString());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password is incorrect")
        void shouldThrowExceptionWhenPasswordIncorrect() {
            // Arrange
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest).block())
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid email or password");

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
            verify(jwtService, never()).generateToken(anyLong(), anyString());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user status is not ACTIVE")
        void shouldThrowExceptionWhenUserNotActive() {
            // Arrange
            user.setStatus("INACTIVE");
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest).block())
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("User account is not active");

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
            verify(jwtService, never()).generateToken(anyLong(), anyString());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when user status is PENDING")
        void shouldThrowExceptionWhenUserPending() {
            // Arrange
            user.setStatus("PENDING");
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest).block())
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("User account is not active");

            verify(userRepository).findByEmail(loginRequest.getEmail());
            verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        }

        @Test
        @DisplayName("Should login successfully with status active in lowercase")
        void shouldLoginWithActiveLowercase() {
            // Arrange
            user.setStatus("active");
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt.token.here");

            // Act
            LoginResponse response = authService.login(loginRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);

            verify(jwtService).generateToken(user.getId(), user.getEmail());
        }

        @Test
        @DisplayName("Should generate JWT token with correct user details")
        void shouldGenerateJwtWithCorrectDetails() {
            // Arrange
            when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Mono.just(user));
            when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(1L, "john.doe@example.com")).thenReturn("generated.jwt.token");

            // Act
            LoginResponse response = authService.login(loginRequest).block();

            // Assert
            assertThat(response.getAccessToken()).isEqualTo("generated.jwt.token");
            verify(jwtService).generateToken(1L, "john.doe@example.com");
        }
    }
}
