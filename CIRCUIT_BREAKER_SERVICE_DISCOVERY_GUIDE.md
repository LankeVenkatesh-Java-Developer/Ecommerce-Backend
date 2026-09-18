# Circuit Breaker and Service Discovery Implementation Guide

## Overview
This document describes the implementation of Circuit Breaker and Service Discovery patterns for the E-commerce microservices architecture using Spring Cloud Netflix Eureka and Resilience4j.

## Architecture Components

### 1. Service Discovery (Eureka Server)
- **Location**: `discovery-service/`
- **Port**: 8761
- **Purpose**: Central service registry for all microservices
- **Key Features**:
  - Service registration and discovery
  - Health monitoring of registered services
  - Load balancing support

### 2. Circuit Breaker (Resilience4j)
- **Purpose**: Prevents cascading failures by failing fast when a service is down
- **Implementation**: Applied to all microservices and API Gateway
- **Key Features**:
  - Automatic circuit opening/closing
  - Fallback mechanisms
  - Timeout handling
  - Retry support

## Implementation Details

### Service Discovery Configuration

#### Eureka Server Setup
```xml
<!-- Dependency -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

#### Eureka Client Configuration (All Microservices)
```properties
# Eureka Client Configuration
eureka.client.service-url.default-zone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
```

### Circuit Breaker Configuration

#### Dependencies (All Microservices)
```xml
<!-- Resilience4j Circuit Breaker -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<!-- Spring Cloud OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### Circuit Breaker Settings
```properties
# Circuit Breaker Configuration
resilience4j.circuitbreaker.configs.default.sliding-window-size=10
resilience4j.circuitbreaker.configs.default.failure-rate-threshold=50
resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state=10000
resilience4j.circuitbreaker.configs.default.permitted-number-of-calls-in-half-open-state=5
resilience4j.timelimiter.configs.default.timeout-duration=5000
```

**Configuration Parameters**:
- `sliding-window-size=10`: Number of calls in the sliding window
- `failure-rate-threshold=50`: Circuit opens when 50% of calls fail
- `wait-duration-in-open-state=10000`: Wait 10 seconds before trying again
- `permitted-number-of-calls-in-half-open-state=5`: Allow 5 test calls when half-open
- `timeout-duration=5000`: 5 second timeout for each call

### API Gateway Configuration

#### Service Discovery Integration
```properties
# Gateway Configuration
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```

#### Route Configuration (Using lb://)
```properties
# Gateway Routes (using service discovery)
spring.cloud.gateway.routes[0].id=cart-service
spring.cloud.gateway.routes[0].uri=lb://cart-management-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/cart/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=0
```

**Key Changes**:
- Changed from `http://localhost:8086` to `lb://cart-management-service`
- `lb://` prefix enables load balancing via service discovery
- Service names must match Eureka registered names

## Microservices Configuration

### Services Registered with Eureka
1. **user-management-service** - Port 8081
2. **products-management-service** - Port 8082
3. **admin-management-service** - Port 8083
4. **order-service** - Port 8084
5. **notification-management-service** - Port 8085
6. **cart-management-service** - Port 8086
7. **api-gateway-service** - Port 8087

### Application Class Annotations
All microservices now include:
```java
@SpringBootApplication
@EnableFeignClients
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

## Startup Sequence

### Required Startup Order
1. **Discovery Service** (Port 8761) - MUST START FIRST
2. **All Microservices** (Ports 8081-8086)
3. **API Gateway** (Port 8087)

### Startup Commands
```bash
# Start Discovery Service
cd discovery-service
mvn spring-boot:run

# Start Microservices (in separate terminals)
cd user-management-service
mvn spring-boot:run

cd Products-management-service
mvn spring-boot:run

cd Admin-management-service
mvn spring-boot:run

cd Order-management-service
mvn spring-boot:run

cd notification-management-service
mvn spring-boot:run

cd Cart-management-service
mvn spring-boot:run

# Start API Gateway (last)
cd api-gateway-service
mvn spring-boot:run
```

## Circuit Breaker States

### State Transitions
1. **CLOSED** (Normal)
   - Requests pass through normally
   - Failure rate monitored

2. **OPEN** (Circuit Tripped)
   - All requests fail fast
   - No calls to downstream service
   - Waits for `wait-duration-in-open-state`

3. **HALF-OPEN** (Testing)
   - Limited test calls allowed
   - If successful → CLOSED
   - If failed → OPEN

### Using Circuit Breaker in Code

#### With @CircuitBreaker Annotation
```java
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductServiceClient {
    
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    public Product getProduct(Long id) {
        // Call to downstream service
        return productRepository.findById(id);
    }
    
    public Product getProductFallback(Long id, Exception ex) {
        // Fallback logic
        return Product.getDefault();
    }
}
```

#### With @Retry Annotation
```java
import io.github.resilience4j.retry.annotation.Retry;

@Retry(name = "productService", fallbackMethod = "getProductFallback")
public Product getProductWithRetry(Long id) {
    // Will retry on failure
    return productRepository.findById(id);
}
```

## Monitoring and Health

### Eureka Dashboard
- **URL**: http://localhost:8761
- **Features**:
  - View all registered services
  - Monitor service health
  - View instance information

### Actuator Endpoints
All services expose:
- `/actuator/health` - Health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Metrics including circuit breaker stats

### Circuit Breaker Metrics
```bash
# Check circuit breaker status
curl http://localhost:8087/actuator/circuitbreakers

# Check specific circuit breaker
curl http://localhost:8087/actuator/circuitbreakers/productService
```

## Benefits

### Service Discovery Benefits
- **Dynamic Service Registration**: Services automatically register with Eureka
- **Load Balancing**: Built-in client-side load balancing
- **No Hardcoded URLs**: Services referenced by name, not location
- **Health Monitoring**: Automatic health checks and deregistration
- **Scalability**: Easy to add/remove service instances

### Circuit Breaker Benefits
- **Fault Tolerance**: Prevents cascading failures
- **Fast Failure**: Fails fast instead of hanging
- **Automatic Recovery**: Automatically recovers when services come back
- **Fallback Support**: Graceful degradation
- **Monitoring**: Built-in metrics and monitoring

## Troubleshooting

### Services Not Registering with Eureka
1. Ensure Eureka Server is running on port 8761
2. Check `eureka.client.service-url.default-zone` configuration
3. Verify network connectivity
4. Check Eureka Server logs for registration errors

### Circuit Breaker Not Working
1. Verify Resilience4j dependencies are included
2. Check circuit breaker configuration properties
3. Ensure `@EnableCircuitBreaker` or `@CircuitBreaker` annotations are used
4. Monitor actuator endpoints for circuit breaker state

### API Gateway Routing Issues
1. Verify service names match Eureka registered names (case-sensitive)
2. Check `lb://` prefix is used in route URIs
3. Ensure discovery locator is enabled
4. Verify all target services are registered with Eureka

## Best Practices

1. **Always start Eureka Server first** before other services
2. **Use meaningful service names** that match business functionality
3. **Configure appropriate timeouts** based on service SLAs
4. **Implement fallback methods** for critical operations
5. **Monitor circuit breaker metrics** regularly
6. **Test failure scenarios** to verify circuit breaker behavior
7. **Use different circuit breaker configurations** for different services
8. **Keep sliding window size appropriate** for your traffic patterns

## Future Enhancements

1. **Spring Cloud Config** - Centralized configuration management
2. **Distributed Tracing** - Zipkin/Sleuth for request tracing
3. **API Gateway Rate Limiting** - Prevent abuse
4. **Service Mesh** - Istio for advanced traffic management
5. **Custom Retry Policies** - Per-service retry strategies
6. **Bulkhead Pattern** - Resource isolation
