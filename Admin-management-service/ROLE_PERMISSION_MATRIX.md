# Role-Based Access Control (RBAC) Documentation

## Overview

This document defines the permission matrix for Admin and Super Admin roles in the E-commerce Backend system. The implementation ensures proper separation of concerns and security across all microservices.

## Roles

### ADMIN
Regular administrators with operational/business management permissions.

### SUPER_ADMIN
System administrators with full access including user management and system-level settings.

## Permission Matrix

| Feature/Endpoint | ADMIN | SUPER_ADMIN | Notes |
|------------------|-------|-------------|-------|
| **Dashboard Access** | ✅ | ✅ | Both roles can access admin dashboard |
| **Product Management** | | | |
| - View Products | ✅ | ✅ | `/api/admin/products` |
| - Create Product | ✅ | ✅ | `/api/admin/products` (POST) |
| - Update Product | ✅ | ✅ | `/api/admin/products/{id}` (PUT) |
| - Delete Product | ✅ | ✅ | `/api/admin/products/{id}` (DELETE) |
| - Update Product Status | ✅ | ✅ | `/api/admin/products/{id}/status` (PATCH) |
| - Update Stock | ✅ | ✅ | `/api/admin/products/{id}/stock` (PUT) |
| **Category Management** | | | |
| - View Categories | ✅ | ✅ | `/api/admin/categories` |
| - Create Category | ✅ | ✅ | `/api/admin/categories` (POST) |
| - Update Category | ✅ | ✅ | `/api/admin/categories/{id}` (PUT) |
| - Delete Category | ✅ | ✅ | `/api/admin/categories/{id}` (DELETE) |
| **Order Management** | | | |
| - View All Orders | ✅ | ✅ | `/api/admin/orders` |
| - View Order Details | ✅ | ✅ | `/api/admin/orders/{id}` |
| - Search Orders | ✅ | ✅ | `/api/admin/orders/number/{orderNumber}` |
| - View User Orders | ✅ | ✅ | `/api/admin/orders/user/{userId}` |
| - Update Order Status | ✅ | ✅ | `/api/admin/orders/{id}` (PUT) |
| - Cancel Order | ✅ | ✅ | `/api/admin/orders/{id}/cancel` (POST) |
| - Delete Order | ❌ | ✅ | `/api/admin/orders/{id}` (DELETE) - Super Admin only |
| **Report Generation** | | | |
| - Product Reports | ✅ | ✅ | `/api/admin/reports/products/excel` |
| - Category Reports | ✅ | ✅ | `/api/admin/reports/categories/excel` |
| - Stock Reports | ✅ | ✅ | `/api/admin/reports/stock/excel` |
| **Configuration Management** | | | |
| - View Configurations | ✅ | ✅ | `/api/admin/config` |
| - View Service Configs | ✅ | ✅ | `/api/admin/config/service/{serviceName}` |
| - Update Service Configs | ❌ | ✅ | `/api/admin/config/service/{serviceName}` (PUT) - Super Admin only |
| - View Notification Config | ✅ | ✅ | `/api/admin/config/notification` |
| - Update Notification Config | ❌ | ✅ | `/api/admin/config/notification` (PUT) - Super Admin only |
| - Test Notification | ✅ | ✅ | `/api/admin/config/test-notification` (POST) |
| **User Management** | | | |
| - View All Users | ❌ | ✅ | `/api/admin/users` - Super Admin only |
| - View User Details | ❌ | ✅ | `/api/admin/users/{id}` - Super Admin only |
| - Update User | ❌ | ✅ | `/api/admin/users/{id}` (PUT) - Super Admin only |
| - Delete User | ❌ | ✅ | `/api/admin/users/{id}` (DELETE) - Super Admin only |
| - Update User Role | ❌ | ✅ | `/api/admin/users/{id}/role` (PATCH) - Super Admin only |
| - Update User Status | ❌ | ✅ | `/api/admin/users/{id}/status` (PATCH) - Super Admin only |

## Service-Level Security Configuration

### Admin Management Service (Port 8083)
- **Base Path**: `/api/admin`
- **Authentication**: JWT required for all endpoints
- **Role Requirements**:
  - `/config/**`: ADMIN or SUPER_ADMIN
  - `/reports/**`: ADMIN or SUPER_ADMIN
  - `/products/**`: ADMIN or SUPER_ADMIN
  - `/categories/**`: ADMIN or SUPER_ADMIN
  - `/orders/**`: ADMIN or SUPER_ADMIN
  - `/users/**`: SUPER_ADMIN only

### Products Management Service (Port 8082)
- **Base Path**: `/api`
- **Authentication**:
  - Read operations (GET): Public access
  - Write operations (POST, PUT, DELETE, PATCH): ADMIN or SUPER_ADMIN required
- **Role Requirements**:
  - `POST /api/products/**`: ADMIN or SUPER_ADMIN
  - `PUT /api/products/**`: ADMIN or SUPER_ADMIN
  - `DELETE /api/products/**`: ADMIN or SUPER_ADMIN
  - `PATCH /api/products/**`: ADMIN or SUPER_ADMIN
  - `POST /api/categories/**`: ADMIN or SUPER_ADMIN
  - `PUT /api/categories/**`: ADMIN or SUPER_ADMIN
  - `DELETE /api/categories/**`: ADMIN or SUPER_ADMIN

### Order Management Service (Port 8084)
- **Base Path**: `/api/v1`
- **Authentication**: Required for all order operations
- **Role Requirements**:
  - `GET /api/v1/orders`: ADMIN or SUPER_ADMIN
  - `PUT /api/v1/orders/**`: ADMIN or SUPER_ADMIN
  - `DELETE /api/v1/orders/**`: SUPER_ADMIN only
  - `POST /api/v1/orders/**/cancel`: ADMIN or SUPER_ADMIN

### User Management Service (Port 8081)
- **Base Path**: `/api/v1`
- **Authentication**: Required for user-specific operations
- **Role Requirements**: User management operations are proxied through Admin Service with SUPER_ADMIN restrictions

## Implementation Details

### JWT Token Flow
1. User authenticates via User Management Service: `POST /api/v1/auth/login`
2. JWT token is returned with user role information
3. Token is included in Authorization header: `Bearer {token}`
4. Each service validates the token and extracts user roles
5. Role-based access control is enforced at both service and endpoint levels

### Role Fetching Mechanism
- Admin Service fetches user roles from User Management Service via REST API
- Endpoint: `GET /api/v1/users/{userId}/roles`
- Fallback: If user service is unavailable, defaults to ADMIN role (with warning)
- Role format: `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`

### Proxy Controllers
Admin Service uses proxy controllers to forward requests to appropriate services:
- **ProductManagementController**: Proxies to Products Service (port 8082)
- **CategoryManagementController**: Proxies to Products Service (port 8082)
- **OrderManagementController**: Proxies to Order Service (port 8084)
- **UserManagementController**: Proxies to User Service (port 8081)

### Security Annotations
- `@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")`: Both roles allowed
- `@PreAuthorize("hasRole('SUPER_ADMIN')")`: Super Admin only
- Method-level security on controller methods
- HTTP method-based security in SecurityConfig

## Frontend Integration

### Navigation Permissions
After login, the sidebar/navigation should display:

**For ADMIN users:**
- Dashboard
- Products
- Categories
- Orders
- Reports

**For SUPER_ADMIN users:**
- Dashboard
- Products
- Categories
- Orders
- Reports
- User Management (Super Admin only)
- System Settings (Super Admin only)

### Role-Based UI Components
```javascript
// Example: Check user role in frontend
const userRole = localStorage.getItem('userRole');

if (userRole === 'ADMIN' || userRole === 'SUPER_ADMIN') {
  // Show Products, Categories, Orders
}

if (userRole === 'SUPER_ADMIN') {
  // Show User Management, System Settings
}
```

## Testing Checklist

### ADMIN Role Testing
- [ ] Login as ADMIN user
- [ ] Access Dashboard
- [ ] View Products list
- [ ] Create new Product
- [ ] Update existing Product
- [ ] Delete Product
- [ ] Update Product status
- [ ] Update Product stock
- [ ] View Categories list
- [ ] Create new Category
- [ ] Update Category
- [ ] Delete Category
- [ ] View Orders list
- [ ] View Order details
- [ ] Update Order status
- [ ] Cancel Order
- [ ] Generate Product report
- [ ] Generate Category report
- [ ] Generate Stock report
- [ ] View configurations
- [ ] Attempt to access User Management (should fail with 403)
- [ ] Attempt to update system configurations (should fail with 403)

### SUPER_ADMIN Role Testing
- [ ] Login as SUPER_ADMIN user
- [ ] All ADMIN operations should work
- [ ] Access User Management
- [ ] View all users
- [ ] View user details
- [ ] Update user information
- [ ] Update user role
- [ ] Update user status
- [ ] Delete user
- [ ] Update service configurations
- [ ] Update notification configurations
- [ ] Delete Order (Super Admin only operation)

### Security Testing
- [ ] Test direct API access without token (should fail with 401)
- [ ] Test ADMIN accessing SUPER_ADMIN endpoints (should fail with 403)
- [ ] Test expired token handling (should fail with 401)
- [ ] Test invalid token handling (should fail with 401)
- [ ] Test token refresh flow

## API Endpoint Summary

### Admin Service Endpoints (Port 8083)

#### Product Management
```
GET    /api/admin/products/admin/all
GET    /api/admin/products
GET    /api/admin/products/{id}
POST   /api/admin/products
PUT    /api/admin/products/{id}
DELETE /api/admin/products/{id}
PATCH  /api/admin/products/{id}/status
PUT    /api/admin/products/{id}/stock
```

#### Category Management
```
GET    /api/admin/categories/admin/all
GET    /api/admin/categories
GET    /api/admin/categories/{id}
POST   /api/admin/categories
PUT    /api/admin/categories/{id}
DELETE /api/admin/categories/{id}
```

#### Order Management
```
GET    /api/admin/orders
GET    /api/admin/orders/{orderId}
GET    /api/admin/orders/number/{orderNumber}
GET    /api/admin/orders/user/{userId}
GET    /api/admin/orders/customer/{customerId}
PUT    /api/admin/orders/{orderId}
POST   /api/admin/orders/{orderId}/cancel
DELETE /api/admin/orders/{orderId} (SUPER_ADMIN only)
```

#### User Management (SUPER_ADMIN only)
```
GET    /api/admin/users
GET    /api/admin/users/{id}
PUT    /api/admin/users/{id}
DELETE /api/admin/users/{id}
PATCH  /api/admin/users/{id}/role
PATCH  /api/admin/users/{id}/status
```

#### Reports
```
GET    /api/admin/reports/products/excel
GET    /api/admin/reports/categories/excel
GET    /api/admin/reports/stock/excel
```

#### Configuration
```
GET    /api/admin/config
GET    /api/admin/config/service/{serviceName}
PUT    /api/admin/config/service/{serviceName} (SUPER_ADMIN only)
GET    /api/admin/config/notification
PUT    /api/admin/config/notification (SUPER_ADMIN only)
POST   /api/admin/config/test-notification
```

## Important Notes

1. **Role Hierarchy**: SUPER_ADMIN has all ADMIN permissions plus additional system-level permissions
2. **Default Behavior**: If user service integration fails, users default to ADMIN role (with logging warning)
3. **Token Expiration**: JWT tokens expire after 24 hours (86400000 ms)
4. **CORS**: All services are configured to allow CORS from any origin for development
5. **Security**: All write operations require authentication and appropriate role authorization
6. **Proxy Pattern**: Admin Service acts as a gateway, forwarding requests to appropriate microservices
7. **Consistent Security**: Role checks are enforced at both service level (SecurityConfig) and method level (@PreAuthorize)

## Troubleshooting

### Common Issues

**Issue**: Admin users cannot see Product/Category/Order Management
- **Solution**: Ensure user service integration is enabled and returning correct roles
- **Check**: `user.service.enabled=true` in application.properties
- **Verify**: User role in database is set correctly (ADMIN or SUPER_ADMIN)

**Issue**: 403 Forbidden errors
- **Solution**: Verify JWT token is valid and contains correct role
- **Check**: Token expiration
- **Verify**: Role format in token (ROLE_ADMIN, ROLE_SUPER_ADMIN)

**Issue**: Proxy requests failing
- **Solution**: Ensure target services are running
- **Check**: Service URLs in application.properties
- **Verify**: Network connectivity between services

## Migration Notes

If upgrading from a previous version:
1. Update Admin Service SecurityConfig to include new endpoint permissions
2. Add new proxy controllers (ProductManagementController, CategoryManagementController, OrderManagementController, UserManagementController)
3. Update Products Service SecurityConfig to enforce role-based write operations
4. Update Order Service SecurityConfig to enforce role-based admin operations
5. Update UserServiceImpl to properly fetch and return user roles
6. Update application.properties with new service URLs
7. Test all role-based access control scenarios

## Support

For issues related to role-based access control:
1. Check service logs for authentication/authorization errors
2. Verify JWT token validity and role information
3. Ensure user service is returning correct roles
4. Check SecurityConfig configurations
5. Review Swagger documentation for endpoint-specific requirements
