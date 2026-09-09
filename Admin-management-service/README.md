# Admin Management Service

A Spring Boot microservice for managing product categories, products, and generating Excel reports in an E-Commerce application.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Database Design](#database-design)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Running Locally](#running-locally)
- [Database Setup](#database-setup)
- [Swagger Documentation](#swagger-documentation)
- [Testing](#testing)

## Overview

The Admin Management Service is a standalone microservice responsible for:
- Product Category Management (CRUD operations)
- Product Management (CRUD operations with search/filter)
- Excel Report Generation (Products, Categories, Stock)
- Role-based access control (ADMIN, SUPER_ADMIN)

## Architecture

```
                    Frontend
                       |
                       v
                 API Gateway
                       |
          +------------+-------------+
          |                          |
          v                          v
 User Management MS          Admin Management MS
          |                          |
          v                          v
 User Database              Admin Database
                                   |
                          +--------+--------+
                          |                 |
                       Category          Product
```

**Microservice Principles:**
- Separate service responsibility
- Separate database ownership
- No direct database access between microservices
- REST-based communication
- JWT-based authentication
- Stateless REST APIs
- DTO-based API contracts

## Technologies

- **Java**: 17
- **Spring Boot**: 4.1.1
- **Spring Data JPA**: For database operations
- **Spring Security**: For authentication and authorization
- **JWT (jjwt)**: 0.11.5 for token validation
- **MySQL**: Database
- **Lombok**: For reducing boilerplate code
- **Jakarta Validation**: For request validation
- **Apache POI**: 5.2.3 for Excel report generation
- **Swagger/OpenAPI**: 2.2.0 for API documentation
- **Maven**: Build tool

## Database Design

### Categories Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL, UNIQUE |
| description | VARCHAR(500) | NULLABLE |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

**Indexes:**
- idx_categories_name
- idx_categories_status

### Products Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| category_id | BIGINT | FOREIGN KEY → categories.id |
| name | VARCHAR(200) | NOT NULL |
| description | TEXT | NULLABLE |
| price | DECIMAL(10,2) | NOT NULL |
| stock_quantity | INT | NOT NULL, DEFAULT 0 |
| sku | VARCHAR(50) | NOT NULL, UNIQUE |
| brand | VARCHAR(100) | NULLABLE |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

**Indexes:**
- idx_products_category
- idx_products_sku (UNIQUE)
- idx_products_status
- idx_products_brand

### Enums

**CategoryStatus:**
- ACTIVE
- INACTIVE

**ProductStatus:**
- ACTIVE
- INACTIVE
- OUT_OF_STOCK

## Project Structure

```
admin-management-service/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/venkatesh/it/adminmanagementservice/
│   │   │       ├── AdminManagementServiceApplication.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── JwtConfig.java
│   │   │       │   ├── OpenApiConfig.java
│   │   │       │   ├── RestTemplateConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── CategoryController.java
│   │   │       │   ├── ProductController.java
│   │   │       │   └── ReportController.java
│   │   │       ├── service/
│   │   │       │   ├── CategoryService.java
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── ReportService.java
│   │   │       │   └── impl/
│   │   │       │       ├── CategoryServiceImpl.java
│   │   │       │       ├── ProductServiceImpl.java
│   │   │       │       └── ReportServiceImpl.java
│   │   │       ├── repository/
│   │   │       │   ├── CategoryRepository.java
│   │   │       │   └── ProductRepository.java
│   │   │       ├── model/
│   │   │       │   ├── Category.java
│   │   │       │   ├── Product.java
│   │   │       │   └── enums/
│   │   │       │       ├── CategoryStatus.java
│   │   │       │       └── ProductStatus.java
│   │   │       ├── dto/
│   │   │       │   ├── CategoryRequest.java
│   │   │       │   ├── CategoryResponse.java
│   │   │       │   ├── ProductRequest.java
│   │   │       │   └── ProductResponse.java
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   ├── DuplicateResourceException.java
│   │   │       │   └── ErrorResponse.java
│   │   │       ├── security/
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── AdminPrincipal.java
│   │   │       ├── client/
│   │   │       │   └── UserManagementClient.java
│   │   │       └── util/
│   │   │           └── ExcelReportUtil.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/venkatesh/it/adminmanagementservice/
│               ├── service/
│               │   ├── CategoryServiceTest.java
│               │   └── ProductServiceTest.java
│               └── controller/
│                   └── CategoryControllerTest.java
├── pom.xml
└── README.md
```

## API Endpoints

### Base URL
```
http://localhost:8082/api/admin
```

### Category Management

#### Create Category
```http
POST /categories
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "status": "ACTIVE"
}
```
**Response:** 201 CREATED

#### Get Category by ID
```http
GET /categories/{id}
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Get All Categories
```http
GET /categories?page=0&size=10&sortBy=id&sortDir=ASC
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Search Categories
```http
GET /categories/search?name=Electronics&status=ACTIVE&page=0&size=10
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Update Category
```http
PUT /categories/{id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Electronics Updated",
  "description": "Updated description",
  "status": "ACTIVE"
}
```
**Response:** 200 OK

#### Update Category Status
```http
PATCH /categories/{id}/status?status=INACTIVE
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Delete Category
```http
DELETE /categories/{id}
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 204 NO CONTENT

### Product Management

#### Create Product
```http
POST /products
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "categoryId": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE"
}
```
**Response:** 201 CREATED

#### Get Product by ID
```http
GET /products/{id}
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Get All Products
```http
GET /products?page=0&size=20&sortBy=id&sortDir=ASC
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Search Products
```http
GET /products/search?name=Laptop&categoryId=1&brand=Dell&status=ACTIVE&minPrice=500&maxPrice=1500&page=0&size=20
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Update Product
```http
PUT /products/{id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "categoryId": 1,
  "name": "Laptop Updated",
  "description": "Updated description",
  "price": 1099.99,
  "stockQuantity": 45,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE"
}
```
**Response:** 200 OK

#### Update Product Status
```http
PATCH /products/{id}/status?status=INACTIVE
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Update Stock Quantity
```http
PATCH /products/{id}/stock?quantity=100
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK

#### Delete Product
```http
DELETE /products/{id}
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 204 NO CONTENT

### Report Management

#### Generate Product Report
```http
GET /reports/products/excel
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK (Excel file download)

#### Generate Category Report
```http
GET /reports/categories/excel
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK (Excel file download)

#### Generate Stock Report
```http
GET /reports/stock/excel
Authorization: Bearer {JWT_TOKEN}
```
**Response:** 200 OK (Excel file download)

## Authentication

All API endpoints (except Swagger UI) require JWT authentication.

### Authentication Flow

1. **Obtain JWT Token**: Login via User Management Service
   ```
   POST http://localhost:8081/api/v1/auth/login
   {
     "emailOrMobile": "admin@example.com",
     "password": "Admin@123"
   }
   ```

2. **Use JWT Token**: Include in Authorization header
   ```
   Authorization: Bearer {JWT_TOKEN}
   ```

### Role-Based Access Control

- **ADMIN**: Full access to all endpoints
- **SUPER_ADMIN**: Full access to all endpoints
- **CUSTOMER**: No access (403 Forbidden)

### Security Configuration

- JWT Secret: Configured in `application.properties`
- JWT Expiration: 24 hours (86400000 ms)
- Password Encoding: BCrypt
- CORS: Enabled for all origins (development)

## Error Handling

### Error Response Format

```json
{
  "timestamp": "2026-08-28T13:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Category not found with id: 100",
  "path": "/api/admin/categories/100",
  "validationErrors": {}
}
```

### HTTP Status Codes

- **200 OK**: Successful GET, PUT, PATCH
- **201 CREATED**: Successful POST
- **204 NO CONTENT**: Successful DELETE
- **400 BAD REQUEST**: Validation failure
- **401 UNAUTHORIZED**: Missing or invalid token
- **403 FORBIDDEN**: Insufficient permissions
- **404 NOT FOUND**: Resource not found
- **409 CONFLICT**: Duplicate resource
- **500 INTERNAL SERVER ERROR**: Unexpected error

### Exception Types

- **ResourceNotFoundException**: Resource not found
- **DuplicateResourceException**: Duplicate resource (name, SKU)
- **AccessDeniedException**: Insufficient permissions
- **MethodArgumentNotValidException**: Validation failure

## Running Locally

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Admin-management-service
   ```

2. **Configure Database**
   - Create MySQL database: `admin_management_db`
   - Update `application.properties` with your credentials

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Verify the application**
   - Application URL: http://localhost:8082/api/admin
   - Swagger UI: http://localhost:8082/api/admin/swagger-ui.html

## Database Setup

### Create Database

```sql
CREATE DATABASE admin_management_db;
```

### Database Configuration

The application uses Hibernate `ddl-auto=update` to automatically create/update tables.

**Note:** For production, use Flyway/Liquibase for database migrations.

## Swagger Documentation

Access the interactive API documentation at:

```
http://localhost:8082/api/admin/swagger-ui.html
```

### Using Swagger

1. Open Swagger UI in browser
2. Click "Authorize" button
3. Enter JWT token (format: `Bearer {token}`)
4. Explore and test endpoints

## Testing

### Run Unit Tests

```bash
mvn test
```

### Test Coverage

- **CategoryServiceTest**: Category business logic
- **ProductServiceTest**: Product business logic
- **CategoryControllerTest**: Category API endpoints

### Example Test Command

```bash
mvn test -Dtest=CategoryServiceTest
```

## Validation Rules

### Category Validation

- **name**: Required, max 100 characters, unique
- **description**: Optional, max 500 characters
- **status**: Optional (default: ACTIVE)

### Product Validation

- **categoryId**: Required
- **name**: Required, max 200 characters
- **description**: Optional, max 1000 characters
- **price**: Required, must be > 0, max 8 integer digits, 2 decimal places
- **stockQuantity**: Required, must be >= 0
- **sku**: Required, max 50 characters, unique
- **brand**: Optional, max 100 characters
- **status**: Optional (default: ACTIVE)

## Configuration

### Application Properties

```properties
# Server
server.port=8082
server.servlet.context-path=/api/admin

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/admin_management_db
spring.datasource.username=Venky
spring.datasource.password=Venky@314

# JWT
app.jwt.secret=mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughForHS512Algorithm
app.jwt.expiration-in-ms=86400000

# Logging
logging.level.com.venkatesh.it.adminmanagementservice=DEBUG
```

## Important Notes

- **Soft Delete**: Categories with products cannot be deleted. Use status update instead.
- **SKU Uniqueness**: Enforced at database level
- **Category Status**: Inactive categories cannot be assigned to new products
- **Stock Management**: Stock quantity of 0 automatically sets status to OUT_OF_STOCK
- **BigDecimal**: Used for all monetary values (not double/float)
- **Pagination**: All list endpoints require pagination (no unbounded lists)

## Inter-Service Communication

The Admin Management Service validates JWT tokens issued by the User Management Service. Both services share the same JWT secret for token validation.

## License

Apache License 2.0

## Contact

- **Name**: Venkatesh IT
- **Email**: admin@venkateshit.com
