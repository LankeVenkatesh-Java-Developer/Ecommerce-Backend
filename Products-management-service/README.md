# Products Management Service

A Spring Boot microservice for managing product categories and products with advanced listing, filtering, and search capabilities.

## Technology Stack

- **Java Version**: 17
- **Spring Boot**: 4.1.1
- **Spring Data JPA**: For database operations
- **MySQL**: Database
- **Lombok**: For reducing boilerplate code
- **Spring Security**: For authentication/authorization
- **Spring Validation**: For request validation
- **Mockito**: For unit testing

## Project Structure

```
com.venkatesh.it.productsmanagementservice
├── config              # Security configuration
├── controller          # REST controllers
├── dto                 # Data Transfer Objects
├── entity              # JPA entities
├── exception           # Custom exceptions and global handler
├── repository          # JPA repositories
├── service             # Business logic layer
└── specification       # JPA Specifications for dynamic filtering
```

## Database Configuration

The application is configured to use MySQL. Update the following in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_products?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

## API Documentation

### Categories API

#### Get All Categories
```http
GET /api/categories
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices",
    "active": true
  }
]
```

#### Get Category by ID
```http
GET /api/categories/{id}
```

#### Create Category
```http
POST /api/categories
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices",
  "active": true
}
```

#### Update Category
```http
PUT /api/categories/{id}
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices",
  "active": true
}
```

#### Delete Category
```http
DELETE /api/categories/{id}
```

### Products API

#### Get Products (with pagination, filtering, and search)
```http
GET /api/products?page=0&size=10&sort=name,asc&categoryId=1&search=iphone&status=ACTIVE
```

**Query Parameters:**
- `page` (default: 0) - Page number (must be >= 0)
- `size` (default: 10) - Page size (must be between 1 and 100)
- `sort` (default: name,asc) - Sorting format: field,direction
- `categoryId` (optional) - Filter by category ID
- `search` (optional) - Search in product name and description (case-insensitive)
- `status` (optional) - Filter by product status (ACTIVE, INACTIVE, OUT_OF_STOCK)

**Response:**
```json
{
  "content": [
    {
      "id": 101,
      "name": "iPhone 16",
      "description": "Apple smartphone",
      "price": 79999.00,
      "quantity": 20,
      "categoryId": 3,
      "categoryName": "Mobiles",
      "status": "ACTIVE",
      "imageUrl": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalPages": 3,
  "totalElements": 25,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

#### Get Product by ID (Product Master View)
```http
GET /api/products/{id}
```

**Response:**
```json
{
  "id": 101,
  "name": "iPhone 16",
  "description": "Apple smartphone",
  "price": 79999.00,
  "quantity": 20,
  "status": "ACTIVE",
  "imageUrl": null,
  "category": {
    "id": 3,
    "name": "Mobiles",
    "description": "Mobile phones",
    "active": true
  },
  "createdAt": "2026-08-30T10:30:00",
  "updatedAt": "2026-08-30T10:30:00"
}
```

#### Create Product
```http
POST /api/products
Content-Type: application/json

{
  "name": "iPhone 16",
  "description": "Apple smartphone",
  "price": 79999.00,
  "quantity": 20,
  "categoryId": 3,
  "imageUrl": "https://example.com/image.jpg"
}
```

#### Update Product
```http
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "iPhone 16 Pro",
  "description": "Apple premium smartphone",
  "price": 89999.00,
  "quantity": 15,
  "categoryId": 3,
  "imageUrl": "https://example.com/image.jpg"
}
```

#### Delete Product
```http
DELETE /api/products/{id}
```

#### Update Product Status
```http
PATCH /api/products/{id}/status?status=INACTIVE
```

## Security Configuration

The following endpoints are **publicly accessible** (no authentication required):
- `GET /api/categories`
- `GET /api/categories/**`
- `GET /api/products`
- `GET /api/products/**`

All other endpoints (POST, PUT, DELETE, PATCH) require authentication. Configure Spring Security with your authentication provider (JWT, OAuth2, etc.) as needed.

## CORS Configuration

The application is configured to allow CORS (Cross-Origin Resource Sharing) for frontend integration:

- **Allowed Origins**: All origins (using `allowedOriginPatterns("*")`)
- **Allowed Methods**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Allowed Headers**: All headers
- **Credentials**: Enabled
- **Max Age**: 3600 seconds

To restrict to specific origins, modify the `corsConfigurationSource()` method in `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://your-frontend-domain.com"));
```

## Error Handling

The application uses a global exception handler that returns consistent error responses:

### Resource Not Found (404)
```json
{
  "status": 404,
  "message": "Product not found with id: 101",
  "timestamp": "2026-08-30T10:30:00",
  "path": "/api/products/101"
}
```

### Validation Error (400)
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Product name is required",
    "price": "Price must be greater than 0"
  },
  "timestamp": "2026-08-30T10:30:00",
  "path": "/api/products"
}
```

### Conflict (409)
```json
{
  "status": 409,
  "message": "Cannot delete category with existing products",
  "timestamp": "2026-08-30T10:30:00",
  "path": "/api/categories/1"
}
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Steps

1. Clone the repository
2. Configure database in `application.properties`
3. Run the application:
```bash
mvn spring-boot:run
```

Or build and run:
```bash
mvn clean package
java -jar target/Products-management-service-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=CategoryServiceTest
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=CategoryControllerTest
mvn test -Dtest=ProductControllerTest
```

### Integration Testing with Postman

#### Test Categories
1. Create a category:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics","description":"Electronic devices","active":true}'
```

2. Get all categories:
```bash
curl http://localhost:8080/api/categories
```

#### Test Products
1. Create a product:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"iPhone 16","description":"Apple smartphone","price":79999.00,"quantity":20,"categoryId":1}'
```

2. Get all products with pagination:
```bash
curl "http://localhost:8080/api/products?page=0&size=10"
```

3. Filter by category:
```bash
curl "http://localhost:8080/api/products?categoryId=1"
```

4. Search products:
```bash
curl "http://localhost:8080/api/products?search=iphone"
```

5. Combined search + category filter:
```bash
curl "http://localhost:8080/api/products?search=iphone&categoryId=1"
```

6. Get product by ID:
```bash
curl http://localhost:8080/api/products/1
```

## Features Implemented

### Core Features
- ✅ Category CRUD operations
- ✅ Product CRUD operations
- ✅ Product listing with pagination
- ✅ Category-based filtering
- ✅ Product search (name and description)
- ✅ Combined search + category filter
- ✅ Product master view (detailed product info)
- ✅ Status-based filtering
- ✅ Sorting support

### Technical Features
- ✅ JPA Specifications for dynamic filtering
- ✅ DTO pattern to avoid lazy loading issues
- ✅ Global exception handling
- ✅ Request validation
- ✅ Spring Security configuration
- ✅ Unit tests for service layer
- ✅ Unit tests for controllers
- ✅ Database auto-configuration

## Database Schema

### Categories Table
```sql
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Products Table
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    category_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

## Performance Considerations

- Uses JPA Specifications for database-level filtering (no in-memory filtering)
- Pagination to avoid loading all records
- Lazy loading with DTO mapping to prevent N+1 queries
- Consider adding database indexes on:
  - `categories.name`
  - `products.category_id`
  - `products.status`
  - `products.name` (for search optimization)

## Future Enhancements

- Add JWT authentication for admin operations
- Implement image upload for products
- Add product reviews/ratings
- Implement caching for frequently accessed data
- Add audit logging
- Implement bulk operations
- Add product variants (size, color, etc.)
