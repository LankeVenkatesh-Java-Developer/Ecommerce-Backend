package com.ashok.it.userservice.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleUserNotFound() Tests")
    class HandleUserNotFoundTests {

        @Test
        @DisplayName("Should return 404 status for UserNotFoundException")
        void shouldReturn404ForUserNotFoundException() {
            // Arrange
            UserNotFoundException exception = new UserNotFoundException("User not found with id: 1");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserNotFound(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(404);
            assertThat(response.getBody().get("message")).isEqualTo("User not found with id: 1");
        }

        @Test
        @DisplayName("Should return custom message from exception")
        void shouldReturnCustomMessage() {
            // Arrange
            UserNotFoundException exception = new UserNotFoundException("Custom user not found message");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserNotFound(exception);

            // Assert
            assertThat(response.getBody().get("message")).isEqualTo("Custom user not found message");
        }
    }

    @Nested
    @DisplayName("handleAddressNotFound() Tests")
    class HandleAddressNotFoundTests {

        @Test
        @DisplayName("Should return 404 status for AddressNotFoundException")
        void shouldReturn404ForAddressNotFoundException() {
            // Arrange
            AddressNotFoundException exception = new AddressNotFoundException("Address not found");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAddressNotFound(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(404);
            assertThat(response.getBody().get("message")).isEqualTo("Address not found");
        }

        @Test
        @DisplayName("Should return custom address not found message")
        void shouldReturnCustomAddressMessage() {
            // Arrange
            AddressNotFoundException exception = new AddressNotFoundException("Address with id 5 not found for user 2");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAddressNotFound(exception);

            // Assert
            assertThat(response.getBody().get("message")).isEqualTo("Address with id 5 not found for user 2");
        }
    }

    @Nested
    @DisplayName("handleDuplicateUser() Tests")
    class HandleDuplicateUserTests {

        @Test
        @DisplayName("Should return 409 status for DuplicateUserException")
        void shouldReturn409ForDuplicateUserException() {
            // Arrange
            DuplicateUserException exception = new DuplicateUserException("Email already registered");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDuplicateUser(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(409);
            assertThat(response.getBody().get("message")).isEqualTo("Email already registered");
        }

        @Test
        @DisplayName("Should return conflict status for mobile number duplicate")
        void shouldReturnConflictForMobileDuplicate() {
            // Arrange
            DuplicateUserException exception = new DuplicateUserException("Mobile number already registered");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDuplicateUser(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().get("status")).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("handleInvalidPassword() Tests")
    class HandleInvalidPasswordTests {

        @Test
        @DisplayName("Should return 400 status for InvalidPasswordException")
        void shouldReturn400ForInvalidPasswordException() {
            // Arrange
            InvalidPasswordException exception = new InvalidPasswordException("Password and confirm password do not match");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidPassword(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(400);
            assertThat(response.getBody().get("message")).isEqualTo("Password and confirm password do not match");
        }

        @Test
        @DisplayName("Should return bad request for weak password")
        void shouldReturnBadRequestForWeakPassword() {
            // Arrange
            InvalidPasswordException exception = new InvalidPasswordException("Password is too weak");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidPassword(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().get("message")).isEqualTo("Password is too weak");
        }
    }

    @Nested
    @DisplayName("handleBadCredentials() Tests")
    class HandleBadCredentialsTests {

        @Test
        @DisplayName("Should return 401 status for BadCredentialsException")
        void shouldReturn401ForBadCredentialsException() {
            // Arrange
            BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleBadCredentials(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("status")).isEqualTo(401);
            assertThat(response.getBody().get("message")).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("Should return standardized message regardless of exception message")
        void shouldReturnStandardizedMessage() {
            // Arrange
            BadCredentialsException exception = new BadCredentialsException("Some other error message");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleBadCredentials(exception);

            // Assert
            assertThat(response.getBody().get("message")).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("Should return 401 for null message in BadCredentialsException")
        void shouldReturn401ForNullMessage() {
            // Arrange
            BadCredentialsException exception = new BadCredentialsException(null);

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleBadCredentials(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().get("message")).isEqualTo("Invalid email or password");
        }
    }

    @Nested
    @DisplayName("Response Structure Tests")
    class ResponseStructureTests {

        @Test
        @DisplayName("Should return response with status and message fields")
        void shouldReturnResponseWithStatusAndMessage() {
            // Arrange
            UserNotFoundException exception = new UserNotFoundException("Test message");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserNotFound(exception);

            // Assert
            assertThat(response.getBody()).containsKeys("status", "message");
            assertThat(response.getBody()).hasSize(2);
        }

        @Test
        @DisplayName("Should return status as integer")
        void shouldReturnStatusAsInteger() {
            // Arrange
            AddressNotFoundException exception = new AddressNotFoundException("Test");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAddressNotFound(exception);

            // Assert
            assertThat(response.getBody().get("status")).isInstanceOf(Integer.class);
        }

        @Test
        @DisplayName("Should return message as string")
        void shouldReturnMessageAsString() {
            // Arrange
            DuplicateUserException exception = new DuplicateUserException("Test message");

            // Act
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDuplicateUser(exception);

            // Assert
            assertThat(response.getBody().get("message")).isInstanceOf(String.class);
        }
    }
}
