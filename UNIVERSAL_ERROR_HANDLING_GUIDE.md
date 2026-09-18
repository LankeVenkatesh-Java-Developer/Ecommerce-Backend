# Universal Error Handling Guide

## Overview
This document describes the universal error handling implementation for the E-commerce microservices architecture. All services now use a consistent `ApiResponse` DTO for standardized error responses, making it easier for the frontend to handle errors uniformly.

## Components

### 1. Common DTO Library
**Location**: `common-dto/`
**Purpose**: Shared DTOs for consistent API responses across all microservices

#### ApiResponse DTO
```java
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
}
```

**Key Features**:
- Generic type support for any data type
- Builder pattern for easy construction
- Null fields excluded from JSON response
- Consistent structure across all services

### 2. Static Factory Methods

#### Success Responses
```java
// Simple success with data
ApiResponse.success(data)

// Success with custom message
ApiResponse.success("Operation completed", data)
```

#### Error Responses
```java
// Simple error message
ApiResponse.error("Something went wrong")

// Error with code and message
ApiResponse.error("VALIDATION_ERROR", "Invalid input")

// Error with code, message, and errors list
ApiResponse.error("VALIDATION_ERROR", "Invalid input", errorsList)

// Error with service and path information
ApiResponse.error("SERVICE_ERROR", "Service unavailable", "user-service", "/api/users")
```

#### Circuit Breaker Specific Responses
```java
// Service unavailable
ApiResponse.serviceUnavailable("user-management-service")

// Circuit breaker open
ApiResponse.circuitBreakerOpen("products-management-service")

// Timeout
ApiResponse.timeout("cart-management-service")
```

## Error Response Format

### Success Response Example
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "id": 1,
    "name": "Product Name"
  },
  "timestamp": "2026-09-17T13:30:00"
}
```

### Error Response Example
```json
{
  "success": false,
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "errors": [
    "name: Name is required",
    "email: Invalid email format"
  ],
  "timestamp": "2026-09-17T13:30:00",
  "path": "/api/v1/users"
}
```

### Circuit Breaker Error Response Example
```json
{
  "success": false,
  "errorCode": "CIRCUIT_BREAKER_OPEN",
  "message": "Service is currently experiencing issues. Circuit breaker is open. Please try again later.",
  "service": "products-management-service",
  "timestamp": "2026-09-17T13:30:00",
  "path": "/api/products/1"
}
```

## Global Exception Handlers

All microservices have updated `GlobalExceptionHandler` classes with circuit breaker exception handling:

### Added Exception Handlers

#### 1. Circuit Breaker Open
```java
@ExceptionHandler(CallNotPermittedException.class)
public ResponseEntity<ApiResponse<?>> handleCircuitBreakerOpen(
    CallNotPermittedException ex, 
    HttpServletRequest request
) {
    ApiResponse<?> response = ApiResponse.circuitBreakerOpen("service-name");
    response.setPath(request.getRequestURI());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
}
```

**Response Code**: `503 Service Unavailable`
**Error Code**: `CIRCUIT_BREAKER_OPEN`

#### 2. Timeout
```java
@ExceptionHandler(TimeoutException.class)
public ResponseEntity<ApiResponse<?>> handleTimeout(
    TimeoutException ex, 
    HttpServletRequest request
) {
    ApiResponse<?> response = ApiResponse.timeout("service-name");
    response.setPath(request.getRequestURI());
    return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
}
```

**Response Code**: `504 Gateway Timeout`
**Error Code**: `TIMEOUT`

#### 3. Service Unavailable
```java
@ExceptionHandler(ResourceAccessException.class)
public ResponseEntity<ApiResponse<?>> handleServiceUnavailable(
    ResourceAccessException ex, 
    HttpServletRequest request
) {
    ApiResponse<?> response = ApiResponse.serviceUnavailable("Downstream Service");
    response.setPath(request.getRequestURI());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
}
```

**Response Code**: `503 Service Unavailable`
**Error Code**: `SERVICE_UNAVAILABLE`

## Services Updated

All microservices now include:
- ✓ Products Management Service
- ✓ User Management Service
- ✓ Cart Management Service
- ✓ Order Management Service
- ✓ Admin Management Service
- ✓ Notification Management Service
- ✓ API Gateway Service

## Frontend Integration

### Error Handling Pattern
```javascript
async function fetchProducts() {
  try {
    const response = await fetch('/api/products');
    const result = await response.json();
    
    if (result.success) {
      // Handle success
      displayProducts(result.data);
    } else {
      // Handle error
      displayError(result);
    }
  } catch (error) {
    // Handle network errors
    displayNetworkError(error);
  }
}

function displayError(apiResponse) {
  const { errorCode, message, errors, service } = apiResponse;
  
  switch (errorCode) {
    case 'CIRCUIT_BREAKER_OPEN':
      showServiceUnavailableMessage(service);
      break;
    case 'TIMEOUT':
      showTimeoutMessage(service);
      break;
    case 'VALIDATION_ERROR':
      showValidationErrors(errors);
      break;
    default:
      showGenericError(message);
  }
}
```

### Error Code Reference

| Error Code | HTTP Status | Description | Frontend Action |
|------------|-------------|-------------|-----------------|
| `CIRCUIT_BREAKER_OPEN` | 503 | Service circuit breaker is open | Show "Service temporarily unavailable" message with retry option |
| `TIMEOUT` | 504 | Request timed out | Show "Request timeout" message with retry option |
| `SERVICE_UNAVAILABLE` | 503 | Downstream service unavailable | Show "Service unavailable" message |
| `VALIDATION_ERROR` | 400 | Input validation failed | Show field-specific validation errors |
| `NOT_FOUND` | 404 | Resource not found | Show "Resource not found" message |
| `INTERNAL_ERROR` | 500 | Internal server error | Show generic error message |
| `INVALID_ARGUMENT` | 400 | Invalid argument provided | Show specific error message |

## Installation

### Build Common DTO
```bash
cd common-dto
mvn clean install
```

### Add Dependency to Services
Already added to all services:
```xml
<dependency>
    <groupId>com.venkatesh.it</groupId>
    <artifactId>common-dto</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage in Controllers

### Success Response
```java
@GetMapping("/products/{id}")
public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
    Product product = productService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(product));
}
```

### Error Response
```java
@PostMapping("/products")
public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductDTO dto) {
    Product product = productService.create(dto);
    return ResponseEntity.ok(ApiResponse.success("Product created successfully", product));
}
```

### Using ApiResponse in Service Layer
```java
@Service
public class ProductService {
    
    public ApiResponse<Product> getProduct(Long id) {
        try {
            Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            return ApiResponse.success(product);
        } catch (ResourceNotFoundException e) {
            return ApiResponse.error("NOT_FOUND", e.getMessage());
        }
    }
}
```

## Benefits

### For Frontend Developers
- **Consistent Structure**: All errors follow the same format
- **Clear Error Codes**: Easy to identify and handle specific error types
- **Service Information**: Know which service caused the error
- **Timestamp**: Track when errors occurred
- **Validation Errors**: Detailed field-level error messages

### For Backend Developers
- **Centralized Error Handling**: Global exception handlers catch all errors
- **Type Safety**: Generic type support for any data type
- **Builder Pattern**: Easy to construct responses
- **Circuit Breaker Integration**: Automatic handling of circuit breaker states
- **Reduced Boilerplate**: Static factory methods simplify response creation

### For System Monitoring
- **Consistent Logging**: All errors logged in same format
- **Error Tracking**: Easy to track error codes and frequencies
- **Service Identification**: Know which service is failing
- **Path Information**: Know which endpoint caused the error

## Testing

### Test Success Response
```java
@Test
public void testSuccessResponse() {
    Product product = new Product(1L, "Test Product");
    ApiResponse<Product> response = ApiResponse.success(product);
    
    assertTrue(response.isSuccess());
    assertEquals("Operation successful", response.getMessage());
    assertNotNull(response.getData());
}
```

### Test Error Response
```java
@Test
public void testErrorResponse() {
    ApiResponse<?> response = ApiResponse.error("VALIDATION_ERROR", "Invalid input");
    
    assertFalse(response.isSuccess());
    assertEquals("VALIDATION_ERROR", response.getErrorCode());
    assertEquals("Invalid input", response.getMessage());
}
```

### Test Circuit Breaker Response
```java
@Test
public void testCircuitBreakerResponse() {
    ApiResponse<?> response = ApiResponse.circuitBreakerOpen("test-service");
    
    assertFalse(response.isSuccess());
    assertEquals("CIRCUIT_BREAKER_OPEN", response.getErrorCode());
    assertEquals("test-service", response.getService());
}
```

## Best Practices

1. **Always use ApiResponse** for all API responses
2. **Include specific error codes** for different error scenarios
3. **Add service name** when calling downstream services
4. **Include path information** for debugging
5. **Use validation errors list** for field-level validation
6. **Handle circuit breaker states** gracefully in frontend
7. **Log errors with context** for monitoring
8. **Provide user-friendly messages** for display

## Troubleshooting

### Common Issues

#### 1. Common DTO Not Found
**Error**: `Cannot find symbol: class ApiResponse`
**Solution**: Ensure `common-dto` is built and installed: `mvn clean install` in common-dto directory

#### 2. Circuit Breaker Not Triggering
**Error**: Circuit breaker exceptions not being caught
**Solution**: Ensure Resilience4j dependencies are added and circuit breaker is configured

#### 3. Inconsistent Error Responses
**Error**: Some endpoints returning different error formats
**Solution**: Ensure all controllers use `ApiResponse` and exceptions are handled by `GlobalExceptionHandler`

## Future Enhancements

1. **Internationalization (i18n)**: Support for multiple languages in error messages
2. **Error Code Registry**: Centralized registry of all error codes
3. **Custom Error Pages**: HTML error pages for different error scenarios
4. **Error Analytics**: Dashboard for error tracking and visualization
5. **Retry Logic**: Automatic retry for transient errors
6. **Rate Limiting**: Include rate limit information in error responses
