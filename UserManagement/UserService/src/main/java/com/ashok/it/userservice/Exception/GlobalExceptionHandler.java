package com.ashok.it.userservice.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleUserNotFound(
            UserNotFoundException ex) {

        log.warn("UserNotFoundException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAddressNotFound(
            AddressNotFoundException ex) {

        log.warn("AddressNotFoundException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDuplicateUser(
            DuplicateUserException ex) {

        log.warn("DuplicateUserException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidPassword(
            InvalidPasswordException ex) {

        log.warn("InvalidPasswordException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBadCredentials(
            BadCredentialsException ex) {

        log.warn("BadCredentialsException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();

        response.put("status", 401);
        response.put("message", "Invalid email or password");

        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgument(
            IllegalArgumentException ex) {

        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Configuration error: " + ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(
            Exception ex) {

        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Internal server error: " + ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response));
    }
}