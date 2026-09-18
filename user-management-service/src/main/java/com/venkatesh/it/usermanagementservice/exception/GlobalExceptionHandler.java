package com.venkatesh.it.usermanagementservice.exception;

import com.venkatesh.it.common.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponse<?>> handleCircuitBreakerOpen(CallNotPermittedException ex, HttpServletRequest request) {
        ApiResponse<?> response = ApiResponse.circuitBreakerOpen("user-management-service");
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<?>> handleTimeout(TimeoutException ex, HttpServletRequest request) {
        ApiResponse<?> response = ApiResponse.timeout("user-management-service");
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleServiceUnavailable(ResourceAccessException ex, HttpServletRequest request) {
        ApiResponse<?> response = ApiResponse.serviceUnavailable("Downstream Service");
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid request parameters")
                .validationErrors(errors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        String message = "Invalid request body format";
        Throwable cause = ex.getCause();
        if (cause != null) {
            if (cause instanceof com.fasterxml.jackson.databind.JsonMappingException) {
                message = "Invalid JSON format: " + cause.getMessage();
            } else if (cause instanceof com.fasterxml.jackson.core.JsonParseException) {
                message = "Invalid JSON syntax: " + cause.getMessage();
            } else {
                message = "Request body parsing error: " + cause.getMessage();
            }
        }
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    private static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> validationErrors;
    }
}
