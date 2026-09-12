# Frontend Integration Guide

## Microservices Architecture Overview

The e-commerce platform consists of 5 microservices:

| Service | Port | Base URL | Description |
|---------|------|----------|-------------|
| User Management Service | 8081 | http://localhost:8081/api/v1 | User authentication, profiles, addresses |
| Products Management Service | 8082 | http://localhost:8082 | Products, categories, inventory |
| Admin Management Service | 8083 | http://localhost:8083/api/admin | Admin dashboard, reports, configuration |
| Order Management Service | 8084 | http://localhost:8084/api/v1 | Orders, payments, shipping |
| Notification Management Service | 8085 | http://localhost:8085/api/notifications | Email & WhatsApp notifications |

## Authentication Flow

### JWT Token Management
All services (except Notification) use JWT authentication with the same secret key.

**Login Flow:**
1. Frontend sends credentials to User Service: `POST /api/v1/auth/login`
2. User Service returns JWT token
3. Store token in localStorage/cookies
4. Include token in Authorization header: `Bearer {token}`

**Token Refresh:**
- Token expiration: 24 hours (86400000 ms)
- Implement refresh logic before expiration

## API Endpoints Summary

### User Management Service (Port 8081)

**Authentication:**
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh JWT token

**User Management:**
- `GET /api/v1/users/profile` - Get current user profile
- `PUT /api/v1/users/profile` - Update user profile
- `GET /api/v1/users/{id}` - Get user by ID (Admin only)
- `GET /api/v1/users` - List all users (Admin only)

**Address Management:**
- `GET /api/v1/users/addresses` - Get user addresses
- `POST /api/v1/users/addresses` - Add new address
- `PUT /api/v1/users/addresses/{id}` - Update address
- `DELETE /api/v1/users/addresses/{id}` - Delete address

### Products Management Service (Port 8082)

**Categories:**
- `GET /categories` - List all categories
- `GET /categories/{id}` - Get category by ID
- `POST /categories` - Create category (Admin)
- `PUT /categories/{id}` - Update category (Admin)
- `DELETE /categories/{id}` - Delete category (Admin)

**Products:**
- `GET /products` - List products with filters
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create product (Admin)
- `PUT /products/{id}` - Update product (Admin)
- `DELETE /products/{id}` - Delete product (Admin)

**Product Filters:**
- Query params: `name`, `category`, `minPrice`, `maxPrice`, `inStock`, `page`, `size`, `sortBy`

### Order Management Service (Port 8084)

**Orders:**
- `POST /api/v1/orders` - Create new order
- `GET /api/v1/orders/{id}` - Get order by ID
- `GET /api/v1/orders` - List user orders
- `PUT /api/v1/orders/{id}` - Update order
- `DELETE /api/v1/orders/{id}` - Cancel order

**Payments:**
- `POST /api/v1/orders/{orderId}/payment` - Process payment (Razorpay)
- `GET /api/v1/orders/{orderId}/payment` - Get payment status

### Admin Management Service (Port 8083)

**Reports:**
- `GET /api/admin/reports/products/excel` - Product report (Excel)
- `GET /api/admin/reports/categories/excel` - Category report (Excel)
- `GET /api/admin/reports/stock/excel` - Stock report (Excel)

**Configuration Management:**
- `GET /api/admin/config` - Get all configurations
- `GET /api/admin/config/service/{serviceName}` - Get service configs
- `PUT /api/admin/config/service/{serviceName}` - Update service config (Super Admin)
- `GET /api/admin/config/notification` - Get notification config
- `PUT /api/admin/config/notification` - Update notification config (Super Admin)
- `POST /api/admin/config/test-notification` - Test notification service

### Notification Management Service (Port 8085)

**Notifications:**
- `POST /api/notifications/send` - Send generic notification
- `POST /api/notifications/order-created` - Order creation notification
- `POST /api/notifications/order-delivered` - Order delivery notification
- `POST /api/notifications/order-cancelled` - Order cancellation notification
- `POST /api/notifications/offer-update` - Offer update notification

**Notification Request Body:**
```json
{
  "recipientEmail": "customer@example.com",
  "recipientPhone": "+1234567890",
  "customerName": "John Doe",
  "notificationType": "ORDER_CREATED",
  "channel": "BOTH",
  "orderId": "ORD-12345",
  "orderDetails": "Product details",
  "offerDetails": "Offer details"
}
```

## Frontend Implementation Requirements

### 1. Authentication State Management
- Store JWT token securely
- Implement token refresh logic
- Handle token expiration
- Protect routes requiring authentication

### 2. API Client Configuration
Create an API client with:
- Base URL configuration per service
- Request interceptor to add JWT token
- Response interceptor for error handling
- Retry logic for failed requests

### 3. Error Handling
- 401 Unauthorized: Redirect to login
- 403 Forbidden: Show access denied
- 404 Not Found: Show resource not found
- 500 Server Error: Show server error message

### 4. Loading States
- Show loading spinners during API calls
- Disable buttons during form submissions
- Implement skeleton loaders for lists

### 5. User Roles
- **USER**: Can browse products, place orders, manage profile
- **ADMIN**: Can manage products, view reports, manage users
- **SUPER_ADMIN**: Full access including configuration management

## CORS Configuration

All services are configured to allow CORS. Ensure your frontend origin is whitelisted if needed.

## Swagger Documentation

Access API documentation at:
- User Service: http://localhost:8081/swagger-ui.html
- Products Service: http://localhost:8082/swagger-ui.html
- Admin Service: http://localhost:8083/api/admin/swagger-ui.html
- Order Service: http://localhost:8084/swagger-ui.html
- Notification Service: http://localhost:8085/swagger-ui.html

## Environment Variables

Create `.env` file in frontend:

```env
VITE_API_USER_SERVICE=http://localhost:8081/api/v1
VITE_API_PRODUCTS_SERVICE=http://localhost:8082
VITE_API_ADMIN_SERVICE=http://localhost:8083/api/admin
VITE_API_ORDER_SERVICE=http://localhost:8084/api/v1
VITE_API_NOTIFICATION_SERVICE=http://localhost:8085/api/notifications
```

## Recommended Frontend Tech Stack

- **Framework**: React.js / Vue.js / Angular
- **State Management**: Redux / Vuex / NgRx
- **HTTP Client**: Axios / Fetch API
- **Routing**: React Router / Vue Router / Angular Router
- **UI Components**: Material-UI / Vuetify / Angular Material
- **Form Handling**: React Hook Form / VeeValidate / Angular Reactive Forms

## Example API Call (Axios)

```javascript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8081/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add request interceptor
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('jwtToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Add response interceptor
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Example: Login
const login = async (email, password) => {
  const response = await apiClient.post('/auth/login', { email, password });
  localStorage.setItem('jwtToken', response.data.token);
  return response.data;
};
```

## Testing Checklist

- [ ] User registration and login flow
- [ ] Product browsing and filtering
- [ ] Add to cart and checkout
- [ ] Order placement and payment
- [ ] Order history and tracking
- [ ] Profile and address management
- [ ] Admin dashboard access
- [ ] Report generation
- [ ] Configuration management (Super Admin)
- [ ] Notification triggers

## Production Deployment

For production:
1. Update all service URLs to production endpoints
2. Enable HTTPS
3. Configure proper CORS origins
4. Use environment-specific configuration
5. Implement proper logging and monitoring
6. Set up CDN for static assets
7. Configure rate limiting
8. Enable caching strategies

## Support

For API issues, check:
1. Service logs in respective service directories
2. Swagger documentation for endpoint details
3. Database connectivity
4. JWT token validity
