package com.venkatesh.it.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String service;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, String service, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .service(service)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> serviceUnavailable(String service) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode("SERVICE_UNAVAILABLE")
                .message("Service temporarily unavailable. Please try again later.")
                .service(service)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> circuitBreakerOpen(String service) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode("CIRCUIT_BREAKER_OPEN")
                .message("Service is currently experiencing issues. Circuit breaker is open. Please try again later.")
                .service(service)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> timeout(String service) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode("TIMEOUT")
                .message("Request timed out. Please try again.")
                .service(service)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
