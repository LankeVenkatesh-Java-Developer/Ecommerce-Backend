# Admin Panel Architecture Documentation

## Overview

This document describes the complete architecture of the Admin Panel for the E-commerce microservices application, including role-based access control (RBAC), service communication, and permission matrix.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     React Frontend (Port 5173)                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Customer UI  │  │ Admin UI     │  │ Public Pages │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ JWT Token (Bearer Auth)
                              │
                    ┌─────────▼─────────┐
                    │  API Gateway      │
                    │  Port: 8080       │
                    │  JWT Validation   │
                    │  CORS Config      │
                    │  Route Management │
                    └─────────┬─────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
│ User Service   │  │ Admin Service   │  │ Product Service │
│ Port: 8081     │  │ Port: 8083      │  │ Port: 8082      │
│ /api/v1        │  │ /api/admin      │  │ /api            │
└────────────────┘  └────────────────┘  └────────────────┘
        │                     │                     │
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
│ Order Service  │  │ Cart Service    │  │ Notification    │
│ Port: 8084     │  │ Port: 8086      │  │ Port: 8085      │
│ /api/v1        │  │ /cart           │  │ /api/notify     │
└────────────────┘  └────────────────┘  └────────────────┘
```

## Services

### 0. API Gateway Service (Port 8080)
- **Base Path**: All routes pass through Gateway
- **Responsibilities**: Central entry point, JWT validation, CORS, routing

**Key Features**:
- JWT token validation at Gateway level
- Automatic routing to appropriate microservices
- CORS configuration for frontend
- User context propagation (X-User-Id, X-User-Role headers)
- Excluded paths for public endpoints (login, register, etc.)

**Routes**:
- `/api/v1/**` (except orders) → User Service
- `/api/v1/orders/**` → Order Service
- `/api/admin/**` → Admin Service
- `/api/**` (except /api/v1 and /api/admin) → Products Service
- `/cart/**` → Cart Service
- `/api/notifications/**` → Notification Service

### 1. User Management Service (Port 8081)
- **Base Path**: `/api/v1`
- **Database**: `user_management_db`
- **Responsibilities**: Authentication, User management, Role management

**Key Endpoints**:
- `POST /auth/login` - User authentication (returns JWT with role)
- `GET /users/me` - Current user profile
- `GET /users` - Get all users (ADMIN/SUPER_ADMIN)
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user
- `PATCH /users/{id}/status` - Update user status (ADMIN/SUPER_ADMIN)
- `PATCH /users/{id}/role` - Update user role (SUPER_ADMIN only)
- `DELETE /users/{id}` - Delete user (SUPER_ADMIN only)
- `GET /users/{id}/roles` - Get user roles

### 2. Admin Management Service (Port 8083)
- **Base Path**: `/api/admin`
- **Database**: `admin_management_db`
- **Responsibilities**: Admin panel proxy, Reports, Configuration

**Key Endpoints**:
- `GET /products` - Get products (ADMIN/SUPER_ADMIN)
- `POST /products` - Create product (ADMIN/SUPER_ADMIN)
- `PUT /products/{id}` - Update product (ADMIN/SUPER_ADMIN)
- `DELETE /products/{id}` - Delete product (ADMIN/SUPER_ADMIN)
- `PATCH /products/{id}/status` - Update product status (ADMIN/SUPER_ADMIN)
- `PUT /products/{id}/stock` - Update stock (ADMIN/SUPER_ADMIN)
- `GET /categories` - Get categories (ADMIN/SUPER_ADMIN)
- `POST /categories` - Create category (ADMIN/SUPER_ADMIN)
- `PUT /categories/{id}` - Update category (ADMIN/SUPER_ADMIN)
- `DELETE /categories/{id}` - Delete category (ADMIN/SUPER_ADMIN)
- `GET /orders` - Get all orders (ADMIN/SUPER_ADMIN)
- `PUT /orders/{id}` - Update order (ADMIN/SUPER_ADMIN)
- `POST /orders/{id}/cancel` - Cancel order (ADMIN/SUPER_ADMIN)
- `DELETE /orders/{id}` - Delete order (SUPER_ADMIN only)
- `GET /users` - Get all users (SUPER_ADMIN only)
- `GET /reports/products/excel` - Product report (ADMIN/SUPER_ADMIN)
- `GET /reports/categories/excel` - Category report (ADMIN/SUPER_ADMIN)
- `GET /reports/stock/excel` - Stock report (ADMIN/SUPER_ADMIN)
- `GET /config` - Get configurations (ADMIN/SUPER_ADMIN)
- `PUT /config/service/{name}` - Update service config (SUPER_ADMIN only)

### 3. Products Management Service (Port 8082)
- **Base Path**: `/api`
- **Database**: `products_management_db`
- **Responsibilities**: Product and Category management

**Security**:
- Read operations (GET): Public access
- Write operations (POST, PUT, DELETE, PATCH): ADMIN/SUPER_ADMIN required

### 4. Order Management Service (Port 8084)
- **Base Path**: `/api/v1`
- **Database**: `order_service_db`
- **Responsibilities**: Order management, Payment processing

**Key Endpoints**:
- `POST /orders` - Create order (authenticated)
- `GET /orders` - Get all orders (ADMIN/SUPER_ADMIN)
- `GET /orders/{id}` - Get order by ID (ADMIN/SUPER_ADMIN or owner)
- `PUT /orders/{id}` - Update order (ADMIN/SUPER_ADMIN)
- `POST /orders/{id}/cancel` - Cancel order (ADMIN/SUPER_ADMIN)
- `DELETE /orders/{id}` - Delete order (SUPER_ADMIN only)

### 5. Cart Management Service (Port 8086)
- **Base Path**: `/cart`
- **Database**: `cart_management_db`
- **Responsibilities**: Shopping cart management

### 6. Notification Management Service (Port 8085)
- **Base Path**: `/api/notify`
- **Responsibilities**: Email and WhatsApp notifications

## Roles and Permissions

### Role Hierarchy

1. **CUSTOMER** - Regular customer with shopping capabilities
2. **ADMIN** - Operational administrator with business management permissions
3. **SUPER_ADMIN** - System administrator with full access including user management

### Permission Matrix

| Feature | CUSTOMER | ADMIN | SUPER_ADMIN |
|---------|----------|-------|-------------|
| **Authentication** |
| Login | ✅ | ✅ | ✅ |
| Register | ✅ | ✅ | ✅ |
| View Profile | ✅ | ✅ | ✅ |
| Update Profile | ✅ | ✅ | ✅ |
| **Product Management** |
| View Products (Public) | ✅ | ✅ | ✅ |
| View Products (Admin) | ❌ | ✅ | ✅ |
| Create Product | ❌ | ✅ | ✅ |
| Update Product | ❌ | ✅ | ✅ |
| Delete Product | ❌ | ✅ | ✅ |
| Update Product Status | ❌ | ✅ | ✅ |
| Update Stock | ❌ | ✅ | ✅ |
| **Category Management** |
| View Categories (Public) | ✅ | ✅ | ✅ |
| View Categories (Admin) | ❌ | ✅ | ✅ |
| Create Category | ❌ | ✅ | ✅ |
| Update Category | ❌ | ✅ | ✅ |
| Delete Category | ❌ | ✅ | ✅ |
| **Order Management** |
| Create Order | ✅ | ❌ | ❌ |
| View Own Orders | ✅ | ❌ | ❌ |
| View All Orders | ❌ | ✅ | ✅ |
| View Order Details | ✅ (own) | ✅ | ✅ |
| Update Order Status | ❌ | ✅ | ✅ |
| Cancel Order | ✅ (own) | ✅ | ✅ |
| Delete Order | ❌ | ❌ | ✅ |
| **User Management** |
| View All Users | ❌ | ❌ | ✅ |
| View User Details | ❌ | ❌ | ✅ |
| Update User | ❌ | ❌ | ✅ |
| Update User Status | ❌ | ❌ | ✅ |
| Update User Role | ❌ | ❌ | ✅ |
| Delete User | ❌ | ❌ | ✅ |
| **Reports** |
| Product Reports | ❌ | ✅ | ✅ |
| Category Reports | ❌ | ✅ | ✅ |
| Stock Reports | ❌ | ✅ | ✅ |
| **Configuration** |
| View Config | ❌ | ✅ | ✅ |
| Update Config | ❌ | ❌ | ✅ |
| Test Notification | ❌ | ✅ | ✅ |

## Frontend Architecture

### Route Protection

**ProtectedRoute Component**:
- `requireAdmin`: Allows ADMIN and SUPER_ADMIN
- `requireSuperAdmin`: Allows SUPER_ADMIN only

**Admin Routes**:
- `/admin` - Dashboard (ADMIN/SUPER_ADMIN)
- `/admin/products` - Product Management (ADMIN/SUPER_ADMIN)
- `/admin/categories` - Category Management (ADMIN/SUPER_ADMIN)
- `/admin/orders` - Order Management (ADMIN/SUPER_ADMIN)
- `/admin/reports` - Reports (ADMIN/SUPER_ADMIN)
- `/admin/users` - User Management (SUPER_ADMIN only)
- `/admin/roles` - Role Management (SUPER_ADMIN only) - *Not implemented yet*
- `/admin/settings` - System Settings (SUPER_ADMIN only) - *Not implemented yet*

### Dynamic Navigation

**AdminLayout** dynamically renders menu items based on user role:

**ADMIN Role**:
- Dashboard
- Categories
- Products
- Orders
- Reports

**SUPER_ADMIN Role**:
- Dashboard
- Categories
- Products
- Orders
- Reports
- Admin Users
- Roles & Permissions
- System Settings

### Authentication Flow

1. User logs in via `/login`
2. Frontend calls `POST /api/v1/auth/login` to User Service
3. User Service validates credentials and returns:
   ```json
   {
     "token": "jwt_token_here",
     "userId": 123,
     "email": "user@example.com",
     "role": "ADMIN"
   }
   ```
4. Frontend stores token and role in localStorage and Redux
5. All subsequent API calls include `Authorization: Bearer {token}` header
6. Each service validates JWT and extracts role for authorization

## Backend Security Implementation

### JWT Token Handling

**Token Generation** (User Service):
- Uses HS512 algorithm
- Secret: `mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughForHS512Algorithm`
- Expiration: 24 hours (86400000 ms)

**Token Validation** (All Services):
- Each service has `JwtAuthenticationFilter`
- Filter extracts token from `Authorization` header
- Token is validated using shared secret
- User ID and roles are extracted from token claims
- Authentication context is set in Spring Security

### Role-Based Authorization

**Method-Level Security**:
- `@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")` - Both roles
- `@PreAuthorize("hasRole('SUPER_ADMIN')")` - Super Admin only
- `@PreAuthorize("isAuthenticated()")` - Any authenticated user
- `@PreAuthorize("#id == authentication.principal.id")` - Resource owner

**URL-Level Security** (SecurityConfig):
- Admin Service: `/products/**`, `/categories/**`, `/orders/**` require ADMIN/SUPER_ADMIN
- Products Service: Write operations require ADMIN/SUPER_ADMIN
- Order Service: Admin operations require ADMIN/SUPER_ADMIN

## Files Changed

### Backend Changes

**Admin Management Service**:
1. `UserServiceImpl.java` - Fixed role fetching to return only actual user role (not default to both ADMIN and SUPER_ADMIN)
2. `ProductManagementController.java` - New proxy controller for product management
3. `CategoryManagementController.java` - New proxy controller for category management
4. `OrderManagementController.java` - New proxy controller for order management
5. `UserManagementController.java` - New proxy controller for user management (SUPER_ADMIN only)
6. `SecurityConfig.java` - Updated with role-based endpoint permissions
7. `application.properties` - Added order service URL configuration

**Order Management Service**:
1. `OrderController.java` - Added `@PreAuthorize` annotations to all endpoints

**Products Management Service**:
1. `SecurityConfig.java` - Updated to require ADMIN/SUPER_ADMIN for write operations

### Frontend Changes

**Components**:
1. `ProtectedRoute.jsx` - Fixed bug (removed CUSTOMER from admin access), added `requireSuperAdmin` prop
2. `AdminLayout.jsx` - Made navigation dynamic based on user role

**Environment Configuration**:
- Updated all service URLs to point to Gateway (http://localhost:8080)

### Gateway Changes (New)

**API Gateway Service (Port 8080)**:
1. `pom.xml` - Spring Cloud Gateway dependencies
2. `ApiGatewayServiceApplication.java` - Route configuration with JWT filter
3. `JwtAuthenticationFilter.java` - JWT validation at Gateway level
4. `CorsConfig.java` - CORS configuration for frontend
5. `application.properties` - Gateway configuration
6. `Dockerfile` - Docker build configuration

**docker-compose.yml**:
- Added API Gateway service
- Updated service URLs to use internal Docker network
- Added GATEWAY_PORT to environment variables

**Pages**:
1. `Orders.jsx` - New order management page
2. `Orders.css` - Styles for order management
3. `Users.jsx` - New user management page (SUPER_ADMIN only)
4. `Users.css` - Styles for user management
5. `Dashboard.jsx` - Updated to fetch real order data

**Services**:
1. `adminManagementService.js` - Added order and user management methods

**API**:
1. `endpoints.js` - Added ADMIN_ORDER_ENDPOINTS and ADMIN_USER_ENDPOINTS

**Routes**:
1. `App.jsx` - Added Orders and Users routes with proper protection

## Environment Configuration

### Backend (.env)
```bash
MYSQL_ROOT_PASSWORD=Venky@314
MYSQL_USER=Venky
MYSQL_PASSWORD=Venky@314
JWT_SECRET=mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughForHS512Algorithm
GATEWAY_PORT=8080
USER_SERVICE_PORT=8081
PRODUCTS_SERVICE_PORT=8082
ADMIN_SERVICE_PORT=8083
ORDER_SERVICE_PORT=8084
CART_SERVICE_PORT=8086
```

### Frontend (.env)
```bash
VITE_API_GATEWAY=http://localhost:8080
VITE_API_USER_SERVICE=http://localhost:8080/api/v1
VITE_API_PRODUCTS_SERVICE=http://localhost:8080/api
VITE_API_CART_SERVICE=http://localhost:8080/cart
VITE_API_ADMIN_SERVICE=http://localhost:8080/api/admin
VITE_API_ORDER_SERVICE=http://localhost:8080/api/v1
VITE_API_NOTIFICATION_SERVICE=http://localhost:8080/api/notifications
```

## How to Run All Services

### Using Docker Compose
```bash
cd C:\Workspace\EcommerceProject\EcomerceBackEnd
docker-compose up -d
```

### Using Individual Services
```bash
# API Gateway
cd api-gateway-service
mvn spring-boot:run

# User Service
cd user-management-service
mvn spring-boot:run

# Products Service
cd Products-management-service
mvn spring-boot:run

# Admin Service
cd Admin-management-service
mvn spring-boot:run

# Order Service
cd Order-management-service
mvn spring-boot:run

# Cart Service
cd Cart-management-service
mvn spring-boot:run

# Notification Service
cd notification-management-service
mvn spring-boot:run
```

### Frontend
```bash
cd C:\Workspace\EcommerceProject\EcomerceFrontEnd\EcommerceFrontend
npm install
npm run dev
```

## Testing

### Test Admin Login
1. Navigate to `http://localhost:5173/login`
2. Login with ADMIN credentials
3. Verify navigation shows: Dashboard, Categories, Products, Orders, Reports
4. Verify `/admin/users` redirects to unauthorized
5. Test product/category/order management operations

### Test Super Admin Login
1. Navigate to `http://localhost:5173/login`
2. Login with SUPER_ADMIN credentials
3. Verify navigation shows: Dashboard, Categories, Products, Orders, Reports, Admin Users, Roles & Permissions, System Settings
4. Test user management operations
5. Verify all ADMIN operations work

### Test Unauthorized Access
1. Try accessing `/admin` without authentication → Redirect to login
2. Try accessing `/admin/users` as ADMIN → Redirect to unauthorized
3. Try calling `GET /api/admin/users` as ADMIN via Postman → 403 Forbidden
4. Try calling `DELETE /api/admin/orders/{id}` as ADMIN → 403 Forbidden

## Security Best Practices Implemented

1. **JWT Token Validation**: All services validate JWT signatures and expiration
2. **Role-Based Authorization**: Both method-level and URL-level security
3. **No Hardcoded Roles**: Roles fetched from User Service via REST API
4. **Token Propagation**: JWT token forwarded through Admin Service to downstream services
5. **Frontend Route Protection**: ProtectedRoute component prevents unauthorized navigation
6. **Backend Authorization**: All protected endpoints verify roles independently
7. **CORS Configuration**: All services configured for cross-origin requests
8. **Error Handling**: Proper 401/403 responses with user-friendly messages
9. **No Sensitive Data Exposure**: Passwords and secrets not exposed in frontend

## Known Limitations

1. **Roles & Permissions Page**: Not implemented yet (placeholder in navigation)
2. **System Settings Page**: Not implemented yet (placeholder in navigation)
3. **Cart and Notification Services**: Empty implementations (not in scope for Admin Panel)
4. **No Refresh Token**: Token must be re-acquired after expiration
5. **Gateway JWT Validation**: Gateway validates JWT but downstream services still validate for defense in depth

## Future Enhancements

1. Add refresh token mechanism
2. Implement fine-grained permissions beyond roles
3. Add audit logging for admin actions
4. Implement Roles & Permissions management page
5. Implement System Settings page
6. Add WebSocket support for real-time updates
7. Implement rate limiting at Gateway
8. Add service discovery (Eureka/Consul)
9. Add comprehensive unit and integration tests
10. Implement OAuth2/OIDC for enterprise SSO

## Troubleshooting

### Common Issues

**Issue**: Admin users cannot see Product/Category/Order Management
- **Solution**: 
  - Verify user role in database is set to ADMIN or SUPER_ADMIN
  - Check Admin Service logs for role fetching errors
  - Ensure `user.service.enabled=true` in application.properties

**Issue**: 403 Forbidden errors
- **Solution**:
  - Verify JWT token is valid and not expired
  - Check token contains correct role in claims
  - Verify SecurityConfig has correct role requirements
  - Check @PreAuthorize annotations on controller methods

**Issue**: Frontend shows "Access denied"
- **Solution**:
  - Check localStorage for token and role
  - Verify Redux state has correct role
  - Check ProtectedRoute component logic
  - Verify API calls include Authorization header

**Issue**: Services cannot communicate
- **Solution**:
  - Verify all services are running
  - Check service URLs in application.properties
  - Verify network connectivity
  - Check firewall settings

## Contact

For issues or questions about the Admin Panel architecture, refer to:
- Admin Service: `ROLE_PERMISSION_MATRIX.md`
- Frontend Integration: `FRONTEND_INTEGRATION_GUIDE.md`
- Service Documentation: Each service's README.md
