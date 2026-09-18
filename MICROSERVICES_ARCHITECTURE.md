# E-Commerce Microservices Architecture

## Overview

This is a production-ready microservices architecture for an e-commerce platform built with Spring Boot and React.js. The system consists of 7 microservices, an API Gateway, and a Service Discovery server, all communicating through REST APIs and service discovery.

## Architecture Diagram

```
┌─────────────────┐
│   React.js      │
│   Frontend      │
└────────┬────────┘
         │
         │ HTTP/HTTPS
         │
┌────────▼────────┐
│  API Gateway    │
│  (Port 8087)    │
└────────┬────────┘
         │
         │ Service Discovery (Eureka)
         │
┌────────▼─────────────────────────────────────────────────────┐
│                    Eureka Server (Port 8761)                   │
└────────┬──────────────────────────────────────────────────────┘
         │
         ├──────────────────────────────────────────────────────┐
         │                      │                               │
┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
│  User Service   │  │ Products Service│  │  Cart Service   │
│  (Port 8081)    │  │  (Port 8082)    │  │  (Port 8086)    │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                      │                               │
         │                      │                               │
┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
│  Admin Service  │  │  Order Service  │  │ Notification    │
│  (Port 8083)    │  │  (Port 8084)    │  │  Service       │
└─────────────────┘  └─────────────────┘  │  (Port 8085)    │
                                            └─────────────────┘
```

## Services

### 1. Discovery Service (Eureka Server)
- **Port**: 8761
- **Purpose**: Service discovery and registration
- **Technology**: Spring Cloud Netflix Eureka
- **Health Check**: http://localhost:8761/actuator/health

### 2. API Gateway Service
- **Port**: 8087
- **Purpose**: Single entry point for all client requests, routing, load balancing, JWT authentication
- **Technology**: Spring Cloud Gateway
- **Features**:
  - JWT authentication filter
  - Service discovery integration
  - Circuit breaker (Resilience4j)
  - CORS configuration

### 3. User Management Service
- **Port**: 8081
- **Context Path**: /api/v1
- **Purpose**: User authentication, authorization, profile management
- **Features**:
  - User registration and login
  - JWT token generation
  - Password reset with OTP
  - Address management
  - Role-based access control
- **Database**: MySQL (user_management_db)

### 4. Products Management Service
- **Port**: 8082
- **Purpose**: Product and category management
- **Features**:
  - Product CRUD operations
  - Category management
  - Product search and filtering
  - Stock management
  - Product status management
- **Database**: MySQL (products_management_db)

### 5. Cart Management Service
- **Port**: 8086
- **Purpose**: Shopping cart functionality
- **Features**:
  - Add/remove items from cart
  - Update item quantities
  - Clear cart
  - Integration with Products service via Feign
- **Database**: MySQL (cart_management_db)

### 6. Order Management Service
- **Port**: 8084
- **Context Path**: /api/v1
- **Purpose**: Order processing and management
- **Features**:
  - Order creation
  - Order status tracking
  - Payment integration (Razorpay)
  - Integration with Cart, Products, and Notification services
- **Database**: MySQL (order_service_db)

### 7. Admin Management Service
- **Port**: 8083
- **Context Path**: /api/admin
- **Purpose**: Administrative operations
- **Features**:
  - User management
  - Product management
  - Order management
  - Category management
  - Report generation (Excel)
  - Configuration management
- **Database**: MySQL (admin_management_db)

### 8. Notification Management Service
- **Port**: 8085
- **Purpose**: Send notifications (Email and WhatsApp)
- **Features**:
  - Email notifications (via SMTP)
  - WhatsApp notifications (via Twilio)
  - Order status notifications
  - Offer updates
- **External Services**: Gmail SMTP, Twilio API

## Technology Stack

### Backend
- **Java**: 17
- **Spring Boot**: 3.2.10
- **Spring Cloud**: 2023.0.3
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service Discovery
- **Spring Cloud OpenFeign**: Inter-service communication
- **Resilience4j**: Circuit Breaker
- **Spring Security**: Authentication & Authorization
- **JWT**: Token-based authentication (jjwt 0.11.5)
- **MySQL**: 8.0 (Databases)
- **Lombok**: Code generation
- **Maven**: Build tool

### Frontend
- **React**: 19.2.8
- **Redux Toolkit**: State management
- **Axios**: HTTP client
- **React Router**: Routing
- **TailwindCSS**: Styling
- **Vite**: Build tool

### Infrastructure
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

## Prerequisites

### For Local Development
- Java 17 or higher
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+ (or use Docker)
- Git

### For Docker Deployment
- Docker 20.10+
- Docker Compose 2.0+

## Setup Instructions

### Option 1: Local Development

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd EcomerceBackEnd
```

#### 2. Install Common DTO
```bash
cd common-dto
mvn clean install
cd ..
```

#### 3. Configure Databases
Create MySQL databases:
```sql
CREATE DATABASE user_management_db;
CREATE DATABASE products_management_db;
CREATE DATABASE cart_management_db;
CREATE DATABASE order_service_db;
CREATE DATABASE admin_management_db;
```

#### 4. Update Configuration Files
Update the following in each service's `application.yml`:
- Database URL, username, password
- JWT secret (use same secret across all services)
- Eureka server URL

#### 5. Start Services in Order
```bash
# Terminal 1 - Discovery Service
cd discovery-service
mvn spring-boot:run

# Terminal 2 - API Gateway
cd api-gateway-service
mvn spring-boot:run

# Terminal 3 - User Service
cd user-management-service
mvn spring-boot:run

# Terminal 4 - Products Service
cd Products-management-service
mvn spring-boot:run

# Terminal 5 - Cart Service
cd Cart-management-service
mvn spring-boot:run

# Terminal 6 - Order Service
cd Order-management-service
mvn spring-boot:run

# Terminal 7 - Admin Service
cd Admin-management-service
mvn spring-boot:run

# Terminal 8 - Notification Service
cd notification-management-service
mvn spring-boot:run
```

#### 6. Start Frontend
```bash
cd ../EcomerceFrontEnd/EcommerceFrontend
npm install
npm run dev
```

### Option 2: Docker Deployment

#### 1. Create .env File
Create a `.env` file in the root directory:
```env
# Gateway
GATEWAY_PORT=8087

# Services
USER_SERVICE_PORT=8081
PRODUCTS_SERVICE_PORT=8082
ADMIN_SERVICE_PORT=8083
ORDER_SERVICE_PORT=8084
NOTIFICATION_SERVICE_PORT=8085
CART_SERVICE_PORT=8086

# MySQL Ports
USER_MYSQL_PORT=3307
PRODUCTS_MYSQL_PORT=3308
ADMIN_MYSQL_PORT=3309
ORDER_MYSQL_PORT=3310
CART_MYSQL_PORT=3311

# MySQL Credentials
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_USER=ecommerce
MYSQL_PASSWORD=ecommercepassword

# JWT Secret
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# Razorpay (Optional)
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Twilio (Optional)
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_WHATSAPP_FROM_NUMBER=+14155238886
```

#### 2. Build and Start Services
```bash
docker-compose up -d --build
```

#### 3. Check Service Status
```bash
docker-compose ps
```

#### 4. View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api-gateway
```

#### 5. Stop Services
```bash
docker-compose down
```

## API Gateway Routes

All client requests go through the API Gateway on port 8087:

| Path | Target Service | Auth Required |
|------|---------------|---------------|
| `/cart/**` | Cart Service | Yes |
| `/api/v1/orders/**` | Order Service | Yes |
| `/api/v1/**` | User Service | Yes |
| `/users/addresses/**` | User Service | Yes |
| `/users/**` | User Service | Yes |
| `/api/products/**` | Products Service | No |
| `/api/categories/**` | Products Service | No |
| `/admin/**` | Admin Service | Yes |
| `/api/notifications/**` | Notification Service | No |

## Service Communication

### Inter-Service Communication via OpenFeign

Services communicate with each other using OpenFeign clients with Eureka service discovery:

**Cart Service → Products Service**
- `ProductsClient.getProductById()`

**Order Service → Cart Service**
- `CartClient.clearCart()`

**Order Service → Products Service**
- `ProductsClient.getProductById()`
- `ProductsClient.updateProductStock()`

**Order Service → Notification Service**
- `NotificationClient.sendOrderCreated()`
- `NotificationClient.sendOrderDelivered()`
- `NotificationClient.sendOrderCancelled()`

**Admin Service → User Service**
- `UserClient.getUserRoles()`
- `UserClient.isValidUser()`

## Authentication Flow

1. User registers/logs in via User Service
2. User Service generates JWT token
3. Client includes JWT token in Authorization header
4. API Gateway validates JWT token
5. Gateway forwards request with user context (X-User-Id, X-User-Role headers)
6. Target service processes request

## Circuit Breaker Configuration

All services have Resilience4j circuit breakers configured:
- Sliding window size: 10
- Failure rate threshold: 50%
- Wait duration in open state: 10s
- Permitted calls in half-open state: 5
- Timeout duration: 5s

## Health Checks

Each service exposes health endpoints:
- `http://localhost:<port>/actuator/health`
- `http://localhost:<port>/actuator/info`
- `http://localhost:<port>/actuator/metrics`

Eureka Dashboard:
- `http://localhost:8761`

## Frontend Configuration

Update `.env` in the frontend:
```env
VITE_API_GATEWAY=http://localhost:8087
VITE_API_USER_SERVICE=http://localhost:8087/api/v1
VITE_API_PRODUCTS_SERVICE=http://localhost:8087/api
VITE_API_CART_SERVICE=http://localhost:8087/cart
VITE_API_ADMIN_SERVICE=http://localhost:8087/admin
VITE_API_ORDER_SERVICE=http://localhost:8087/api/v1
VITE_API_NOTIFICATION_SERVICE=http://localhost:8087/api/notifications
```

## Troubleshooting

### Services Not Registering with Eureka
- Check Eureka server is running: http://localhost:8761
- Verify service names match in `application.yml`
- Check network connectivity
- Review service logs for connection errors

### API Gateway Routing Issues
- Verify service names in gateway routes match Eureka registrations
- Check path predicates and strip prefix configuration
- Review JWT filter excluded paths

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `application.yml`
- Ensure databases exist
- Review connection URL format

### Feign Client Failures
- Verify target service is registered with Eureka
- Check service names in `@FeignClient` annotations
- Review endpoint paths in Feign interfaces
- Check circuit breaker status

### JWT Authentication Errors
- Ensure JWT secret is consistent across all services
- Verify token format and expiration
- Check excluded paths in JWT filter
- Review user role claims in token

## Security Best Practices

1. **Change default passwords** in production
2. **Use strong JWT secrets** (minimum 256 bits for HS512)
3. **Enable HTTPS** in production
4. **Configure CORS** properly for frontend domain
5. **Use environment variables** for sensitive data
6. **Enable Spring Security** on all services
7. **Implement rate limiting** on API Gateway
8. **Regular security updates** for dependencies

## Performance Optimization

1. **Enable database connection pooling**
2. **Configure appropriate JVM heap sizes**
3. **Use caching** for frequently accessed data
4. **Implement API response caching**
5. **Optimize database queries**
6. **Use asynchronous processing** for non-critical operations
7. **Monitor circuit breaker states**

## Monitoring and Logging

All services use structured logging with SLF4J. Configure log levels in `application.yml`:

```yaml
logging:
  level:
    com.venkatesh.it: DEBUG
    org.springframework: INFO
    org.hibernate.SQL: DEBUG
```

## Deployment Checklist

- [ ] Update all database credentials
- [ ] Change JWT secret to strong value
- [ ] Configure email service credentials
- [ ] Set up Twilio for WhatsApp (if needed)
- [ ] Configure Razorpay for payments (if needed)
- [ ] Update CORS configuration for production domain
- [ ] Enable HTTPS
- [ ] Set up monitoring and alerting
- [ ] Configure backup strategy for databases
- [ ] Review and adjust resource limits

## Support

For issues or questions:
1. Check service logs
2. Verify Eureka dashboard
3. Review API Gateway routes
4. Check database connectivity
5. Validate JWT tokens

## License

Proprietary - All rights reserved
