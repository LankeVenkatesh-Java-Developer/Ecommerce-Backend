package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.RegisterRequest;
import com.ashok.it.userservice.Dto.UserResponse;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.DuplicateUserException;
import com.ashok.it.userservice.Exception.InvalidPasswordException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.ashok.it.userservice.Event.UserEventPublisher userEventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setMobileNumber("9876543210");
        registerRequest.setPassword("SecurePass123");
        registerRequest.setConfirmPassword("SecurePass123");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setMobileNumber("9876543210");
        savedUser.setPassword("encodedPassword");
        savedUser.setStatus("ACTIVE");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("registerUser() Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully when all validations pass")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(Mono.just(false));
            when(userRepository.existsByMobileNumber(registerRequest.getMobileNumber())).thenReturn(Mono.just(false));
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

            // Act
            UserResponse response = userService.registerUser(registerRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getFirstName()).isEqualTo("John");
            assertThat(response.getLastName()).isEqualTo("Doe");
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(response.getMobileNumber()).isEqualTo("9876543210");
            assertThat(response.getStatus()).isEqualTo("ACTIVE");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository).existsByMobileNumber(registerRequest.getMobileNumber());
            verify(passwordEncoder).encode(registerRequest.getPassword());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateUserException when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(Mono.just(true));

            // Act & Assert
            assertThatThrownBy(() -> userService.registerUser(registerRequest).block())
                    .isInstanceOf(DuplicateUserException.class)
                    .hasMessage("Email already registered");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository, never()).existsByMobileNumber(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateUserException when mobile number already exists")
        void shouldThrowExceptionWhenMobileNumberExists() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(Mono.just(false));
            when(userRepository.existsByMobileNumber(registerRequest.getMobileNumber())).thenReturn(Mono.just(true));

            // Act & Assert
            assertThatThrownBy(() -> userService.registerUser(registerRequest).block())
                    .isInstanceOf(DuplicateUserException.class)
                    .hasMessage("Mobile number already registered");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository).existsByMobileNumber(registerRequest.getMobileNumber());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw InvalidPasswordException when passwords do not match")
        void shouldThrowExceptionWhenPasswordsDoNotMatch() {
            // Arrange
            registerRequest.setConfirmPassword("DifferentPass123");

            // Act & Assert
            assertThatThrownBy(() -> userService.registerUser(registerRequest).block())
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessage("Password and confirm password do not match");

            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).existsByMobileNumber(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should register user with null last name")
        void shouldRegisterUserWithNullLastName() {
            // Arrange
            registerRequest.setLastName(null);
            savedUser.setLastName(null);
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(Mono.just(false));
            when(userRepository.existsByMobileNumber(registerRequest.getMobileNumber())).thenReturn(Mono.just(false));
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

            // Act
            UserResponse response = userService.registerUser(registerRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getLastName()).isNull();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(Mono.just(false));
            when(userRepository.existsByMobileNumber(registerRequest.getMobileNumber())).thenReturn(Mono.just(false));
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

            // Act
            userService.registerUser(registerRequest).block();

            // Assert
            verify(passwordEncoder).encode("SecurePass123");
        }
    }

    @Nested
    @DisplayName("getUserById() Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when user exists")
        void shouldReturnUserWhenExists() {
            // Arrange
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Mono.just(savedUser));

            // Act
            UserResponse response = userService.getUserById(userId).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(userId).block())
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found with id: " + userId);

            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should return user with all fields populated")
        void shouldReturnUserWithAllFields() {
            // Arrange
            savedUser.setFirstName("Jane");
            savedUser.setLastName("Smith");
            savedUser.setEmail("jane.smith@example.com");
            savedUser.setMobileNumber("8765432109");
            savedUser.setStatus("INACTIVE");
            when(userRepository.findById(1L)).thenReturn(Mono.just(savedUser));

            // Act
            UserResponse response = userService.getUserById(1L).block();

            // Assert
            assertThat(response.getFirstName()).isEqualTo("Jane");
            assertThat(response.getLastName()).isEqualTo("Smith");
            assertThat(response.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(response.getMobileNumber()).isEqualTo("8765432109");
            assertThat(response.getStatus()).isEqualTo("INACTIVE");
        }
    }
}
