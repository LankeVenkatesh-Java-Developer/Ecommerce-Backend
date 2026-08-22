# AdminService Implementation Guide

Based on the UserService reactive microservice architecture, here's a clear guide to implement AdminService with the following features:

## Features
1. Product Categories Management (insert/update/retrieve/delete)
2. Products Management (insert/update/retrieve/delete)
3. Reports Generation (Excel format)

---

## 1. Project Structure

```
AdminService/
├── pom.xml
├── src/main/
│   ├── java/com/ashok/it/adminservice/
│   │   ├── Config/
│   │   │   ├── KafkaConfig.java
│   │   │   ├── KafkaConsumerConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── EurekaConfig.java
│   │   │   ├── ResilienceConfig.java
│   │   │   ├── TracingConfig.java
│   │   │   └── ServiceAuthConfig.java
│   │   ├── Controller/
│   │   │   ├── CategoryController.java
│   │   │   ├── ProductController.java
│   │   │   └── ReportController.java
│   │   ├── Dto/
│   │   │   ├── CategoryRequest.java
│   │   │   ├── CategoryResponse.java
│   │   │   ├── ProductRequest.java
│   │   │   ├── ProductResponse.java
│   │   │   └── ReportRequest.java
│   │   ├── Entity/
│   │   │   ├── Category.java
│   │   │   └── Product.java
│   │   ├── Exception/
│   │   │   ├── CategoryNotFoundException.java
│   │   │   ├── ProductNotFoundException.java
│   │   │   └── ReportGenerationException.java
│   │   ├── Implement/
│   │   │   ├── CategoryServiceImpl.java
│   │   │   ├── ProductServiceImpl.java
│   │   │   └── ReportServiceImpl.java
│   │   ├── Repository/
│   │   │   ├── CategoryRepository.java
│   │   │   └── ProductRepository.java
│   │   ├── Service/
│   │   │   ├── CategoryService.java
│   │   │   ├── ProductService.java
│   │   │   ├── ReportService.java
│   │   │   └── UserServiceClient.java
│   │   ├── Event/
│   │   │   ├── ProductEvent.java
│   │   │   └── ProductEventPublisher.java
│   │   ├── Security/
│   │   │   ├── SecurityConfig.java
│   │   │   └── JwtAuthenticationFilter.java
│   │   └── AdminServiceApplication.java
│   └── resources/
│       └── application.properties
```

---

## 2. pom.xml Dependencies

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.ashok.it</groupId>
    <artifactId>admin-service</artifactId>
    <version>1.0.0</version>
    <name>AdminService</name>
    <description>Admin Service for E-commerce Platform</description>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <!-- Spring WebFlux (Reactive) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        
        <!-- R2DBC for Reactive Database Access -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>
        
        <!-- MySQL R2DBC Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-r2dbc</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Reactive Redis Caching -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        
        <!-- Kafka for Message Queue -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        
        <!-- Eureka Service Discovery -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        
        <!-- Resilience4j Circuit Breaker -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>
        
        <!-- Distributed Tracing -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>
        
        <!-- Spring Boot Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Apache POI for Excel Reports -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>5.2.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

---

## 3. application.properties

```properties
# Application Configuration
spring.application.name=admin-service
server.port=8082

# R2DBC MySQL Configuration
spring.r2dbc.url=r2dbc:mysql://localhost:3306/admin_db
spring.r2dbc.username=root
spring.r2dbc.password=root
spring.r2dbc.pool.initial-size=5
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m

# Redis Configuration (Caching)
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.cache.type=redis
spring.cache.redis.time-to-live=600000

# Kafka Configuration (Message Queue)
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.group-id=admin-service-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.properties.spring.json.trusted.packages=*

# Eureka Service Discovery Configuration
eureka.client.service-url.default-zone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
eureka.instance.lease-renewal-interval-in-seconds=10
eureka.instance.lease-expiration-duration-in-seconds=30

# Resilience4j Circuit Breaker Configuration
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.register-health-indicator=true
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.sliding-window-size=10
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.adminServiceCircuitBreaker.wait-duration-in-open-state=5s

# Resilience4j Retry Configuration
resilience4j.retry.instances.adminServiceRetry.max-attempts=3
resilience4j.retry.instances.adminServiceRetry.wait-duration=1s

# Resilience4j Rate Limiter Configuration
resilience4j.ratelimiter.instances.adminServiceRateLimiter.limit-for-period=100
resilience4j.ratelimiter.instances.adminServiceRateLimiter.limit-refresh-period=1s
resilience4j.ratelimiter.instances.adminServiceRateLimiter.timeout-duration=3s

# Distributed Tracing Configuration
management.tracing.enabled=true
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

# Actuator Endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always

# JWT Configuration
jwt.secret=mySuperSecretKeyForEcommerceAdminService2026Secure
jwt.expiration=86400000

# Logging Configuration
logging.level.com.ashok.it.adminservice=DEBUG
logging.level.org.springframework.r2dbc=DEBUG
```

---

## 4. Entity Classes

### Category.java
```java
package com.ashok.it.adminservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class Category {
    @Id
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

### Product.java
```java
package com.ashok.it.adminservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {
    @Id
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String brand;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

---

## 5. Repository Interfaces

### CategoryRepository.java
```java
package com.ashok.it.adminservice.Repository;

import com.ashok.it.adminservice.Entity.Category;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends R2dbcRepository<Category, Long> {
    Flux<Category> findByActiveTrue();
    Mono<Category> findByIdAndActiveTrue(Long id);
    Mono<Boolean> existsByName(String name);
}
```

### ProductRepository.java
```java
package com.ashok.it.adminservice.Repository;

import com.ashok.it.adminservice.Entity.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Flux<Product> findByCategoryId(Long categoryId);
    Flux<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    Flux<Product> findByActiveTrue();
    Mono<Product> findByIdAndActiveTrue(Long id);
    Mono<Boolean> existsBySku(String sku);
}
```

---

## 6. Service Interfaces

### CategoryService.java
```java
package com.ashok.it.adminservice.Service;

import com.ashok.it.adminservice.Dto.CategoryRequest;
import com.ashok.it.adminservice.Dto.CategoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<CategoryResponse> createCategory(CategoryRequest request);
    Mono<CategoryResponse> updateCategory(Long categoryId, CategoryRequest request);
    Mono<CategoryResponse> getCategoryById(Long categoryId);
    Flux<CategoryResponse> getAllCategories();
    Mono<Void> deleteCategory(Long categoryId);
}
```

### ProductService.java
```java
package com.ashok.it.adminservice.Service;

import com.ashok.it.adminservice.Dto.ProductRequest;
import com.ashok.it.adminservice.Dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<ProductResponse> createProduct(ProductRequest request);
    Mono<ProductResponse> updateProduct(Long productId, ProductRequest request);
    Mono<ProductResponse> getProductById(Long productId);
    Flux<ProductResponse> getAllProducts();
    Flux<ProductResponse> getProductsByCategory(Long categoryId);
    Mono<Void> deleteProduct(Long productId);
}
```

### ReportService.java
```java
package com.ashok.it.adminservice.Service;

import com.ashok.it.adminservice.Dto.ReportRequest;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ReportService {
    Mono<Void> generateProductsReport(ReportRequest request, ServerWebExchange exchange);
    Mono<Void> generateCategoriesReport(ReportRequest request, ServerWebExchange exchange);
    Mono<Void> generateSalesReport(ReportRequest request, ServerWebExchange exchange);
}
```

---

## 7. Controller Examples

### CategoryController.java
```java
package com.ashok.it.adminservice.Controller;

import com.ashok.it.adminservice.Dto.CategoryRequest;
import com.ashok.it.adminservice.Dto.CategoryResponse;
import com.ashok.it.adminservice.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Mono<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            ServerWebExchange exchange) {
        return categoryService.createCategory(request)
                .map(response -> {
                    exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                    return response;
                });
    }

    @PutMapping("/{categoryId}")
    public Mono<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(categoryId, request);
    }

    @GetMapping("/{categoryId}")
    public Mono<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping
    public Flux<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @DeleteMapping("/{categoryId}")
    public Mono<String> deleteCategory(@PathVariable Long categoryId) {
        return categoryService.deleteCategory(categoryId)
                .thenReturn("Category deleted successfully");
    }
}
```

---

## 8. Excel Report Generation (Apache POI)

### ReportServiceImpl.java (Key parts)
```java
package com.ashok.it.adminservice.Implement;

import com.ashok.it.adminservice.Dto.ReportRequest;
import com.ashok.it.adminservice.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Mono<Void> generateProductsReport(ReportRequest request, ServerWebExchange exchange) {
        return productRepository.findAll()
                .collectList()
                .flatMap(products -> {
                    try {
                        Workbook workbook = new XSSFWorkbook();
                        Sheet sheet = workbook.createSheet("Products");

                        // Create header row
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"ID", "Name", "SKU", "Price", "Stock", "Category", "Status"};
                        for (int i = 0; i < headers.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(headers[i]);
                        }

                        // Fill data
                        int rowNum = 1;
                        for (Product product : products) {
                            Row row = sheet.createRow(rowNum++);
                            row.createCell(0).setCellValue(product.getId());
                            row.createCell(1).setCellValue(product.getName());
                            row.createCell(2).setCellValue(product.getSku());
                            row.createCell(3).setCellValue(product.getPrice().doubleValue());
                            row.createCell(4).setCellValue(product.getStockQuantity());
                            row.createCell(5).setCellValue(product.getCategoryId());
                            row.createCell(6).setCellValue(product.getActive() ? "Active" : "Inactive");
                        }

                        // Auto-size columns
                        for (int i = 0; i < headers.length; i++) {
                            sheet.autoSizeColumn(i);
                        }

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        workbook.write(outputStream);
                        workbook.close();

                        byte[] bytes = outputStream.toByteArray();
                        exchange.getResponse().getHeaders().setContentType(
                                MediaType.APPLICATION_OCTET_STREAM);
                        exchange.getResponse().getHeaders().setContentLength(bytes.length);
                        exchange.getResponse().getHeaders().setContentDispositionFormData(
                                "attachment", "products_report_" + 
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx");

                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));

                    } catch (Exception e) {
                        log.error("Error generating products report", e);
                        return Mono.error(new RuntimeException("Failed to generate report"));
                    }
                });
    }
}
```

---

## 9. Communication with UserService

### UserServiceClient.java
```java
package com.ashok.it.adminservice.Service;

import com.ashok.it.adminservice.Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<UserResponse> getUserById(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToMono(UserResponse.class)
                .doOnError(error -> log.error("Error fetching user {}: {}", userId, error.getMessage()));
    }
}
```

---

## 10. Kafka Events

### ProductEvent.java
```java
package com.ashok.it.adminservice.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String eventType;
    private Long productId;
    private Long categoryId;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private LocalDateTime timestamp;
}
```

### ProductEventPublisher.java
```java
package com.ashok.it.adminservice.Event;

import com.ashok.it.adminservice.Config.KafkaConfig;
import com.ashok.it.adminservice.Dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Void> publishProductCreatedEvent(ProductResponse productResponse) {
        return Mono.fromRunnable(() -> {
            ProductEvent event = new ProductEvent();
            event.setEventType("PRODUCT_CREATED");
            event.setProductId(productResponse.getId());
            event.setCategoryId(productResponse.getCategoryId());
            event.setName(productResponse.getName());
            event.setSku(productResponse.getSku());
            event.setPrice(productResponse.getPrice());
            event.setStockQuantity(productResponse.getStockQuantity());
            event.setTimestamp(LocalDateTime.now());
            
            kafkaTemplate.send(KafkaConfig.PRODUCT_TOPIC, event);
            log.info("Published PRODUCT_CREATED event for product: {}", productResponse.getName());
        }).then();
    }
}
```

---

## 11. Database Schema

```sql
CREATE DATABASE admin_db;

USE admin_db;

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE KEY uk_name (name)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    sku VARCHAR(50) UNIQUE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(255),
    brand VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);
```

---

## 12. Key Implementation Notes

1. **Follow the same reactive pattern as UserService** - Use Mono/Flux throughout
2. **Copy configuration files from UserService** - KafkaConfig, RedisConfig, EurekaConfig, etc.
3. **Use Apache POI for Excel generation** - As shown in ReportServiceImpl
4. **Publish Kafka events** on product/category changes for other services to consume
5. **Use WebClient for inter-service communication** with UserService if needed
6. **Apply Resilience4j annotations** on service methods for circuit breaking
7. **Use Redis caching** for frequently accessed categories/products
8. **Implement proper error handling** with reactive operators

---

## 13. Testing Endpoints

```bash
# Create Category
POST http://localhost:8082/api/v1/admin/categories
Content-Type: application/json
{
  "name": "Electronics",
  "description": "Electronic devices",
  "sortOrder": 1
}

# Create Product
POST http://localhost:8082/api/v1/admin/products
Content-Type: application/json
{
  "categoryId": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "sku": "LAP-001",
  "price": 999.99,
  "stockQuantity": 50,
  "brand": "Dell"
}

# Generate Products Report
GET http://localhost:8082/api/v1/admin/reports/products?startDate=2024-01-01&endDate=2024-12-31
```

---

This guide provides a complete blueprint for implementing AdminService following the same reactive microservice architecture as UserService.
