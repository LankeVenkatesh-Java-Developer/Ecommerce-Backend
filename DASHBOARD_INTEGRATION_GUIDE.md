# Dashboard Integration Guide - Products Management

## Problem
Dashboard was calling Admin Management Service (port 8082) which has a separate database, while Postman was calling Products Management Service (port 8083) directly. This caused data inconsistency.

## Solution
Update your dashboard to call Products Management Service directly for product and category operations.

## Service Architecture

**Products Management Service (port 8083)**
- Base URL: `http://localhost:8083/api`
- Handles: Products, Categories
- Database: Oracle (SPRINGAPP schema)

**Admin Management Service (port 8082)** 
- Base URL: `http://localhost:8082/api/admin`
- Handles: User management, Reports
- Database: Oracle (separate schema)

**User Management Service (port 8081)**
- Base URL: `http://localhost:8081/api/v1`
- Handles: Authentication, User profiles
- Database: Oracle (separate schema)

## Dashboard API Integration

### Update Your Dashboard API Calls

**Replace these Admin Service calls:**
```javascript
// OLD - Admin Service (wrong database)
fetch('http://localhost:8082/api/admin/categories')
fetch('http://localhost:8082/api/admin/products')
```

**With these Products Service calls:**
```javascript
// NEW - Products Service (correct database)
fetch('http://localhost:8083/api/categories/admin/all')
fetch('http://localhost:8083/api/products/admin/all?page=0&size=10')
```

### Complete API Reference for Dashboard

#### Categories
```javascript
// Get all categories (including inactive) - for dashboard
GET http://localhost:8083/api/categories/admin/all

// Get active categories only - for home page  
GET http://localhost:8083/api/categories

// Get category by ID
GET http://localhost:8083/api/categories/{id}

// Create category
POST http://localhost:8083/api/categories
Content-Type: application/json
{
  "name": "Electronics",
  "description": "Electronic devices",
  "active": true,
  "status": "ACTIVE"
}

// Update category
PUT http://localhost:8083/api/categories/{id}
Content-Type: application/json
{
  "name": "Electronics Updated",
  "description": "Updated description",
  "active": true,
  "status": "ACTIVE"
}

// Delete category
DELETE http://localhost:8083/api/categories/{id}
```

#### Products
```javascript
// Get all products (no status filtering) - for dashboard
GET http://localhost:8083/api/products/admin/all?page=0&size=10

// Get products with filters - for home page
GET http://localhost:8083/api/products?page=0&size=10&categoryId=1&search=laptop&status=ACTIVE

// Get product by ID
GET http://localhost:8083/api/products/{id}

// Create product
POST http://localhost:8083/api/products
Content-Type: application/json
{
  "name": "Laptop",
  "description": "High performance laptop",
  "price": 999.99,
  "quantity": 10,
  "categoryId": 1,
  "imageUrl": "https://example.com/laptop.jpg"
}

// Update product
PUT http://localhost:8083/api/products/{id}
Content-Type: application/json
{
  "name": "Laptop Pro",
  "description": "Updated laptop",
  "price": 1199.99,
  "quantity": 5,
  "categoryId": 1,
  "imageUrl": "https://example.com/laptop-pro.jpg"
}

// Update product status
PATCH http://localhost:8083/api/products/{id}/status?status=INACTIVE

// Delete product
DELETE http://localhost:8083/api/products/{id}
```

### React Dashboard Example

```javascript
// src/services/dashboardApi.js
const BASE_URL = 'http://localhost:8083/api';

export const dashboardApi = {
  // Categories
  getCategoriesForDashboard: async () => {
    const response = await fetch(`${BASE_URL}/categories/admin/all`);
    return response.json();
  },
  
  createCategory: async (categoryData) => {
    const response = await fetch(`${BASE_URL}/categories`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryData)
    });
    return response.json();
  },
  
  updateCategory: async (id, categoryData) => {
    const response = await fetch(`${BASE_URL}/categories/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryData)
    });
    return response.json();
  },
  
  deleteCategory: async (id) => {
    const response = await fetch(`${BASE_URL}/categories/${id}`, {
      method: 'DELETE'
    });
    return response;
  },

  // Products
  getProductsForDashboard: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const response = await fetch(`${BASE_URL}/products/admin/all?${queryString}`);
    return response.json();
  },
  
  createProduct: async (productData) => {
    const response = await fetch(`${BASE_URL}/products`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productData)
    });
    return response.json();
  },
  
  updateProduct: async (id, productData) => {
    const response = await fetch(`${BASE_URL}/products/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productData)
    });
    return response.json();
  },
  
  deleteProduct: async (id) => {
    const response = await fetch(`${BASE_URL}/products/${id}`, {
      method: 'DELETE'
    });
    return response;
  }
};
```

### Vue Dashboard Example

```javascript
// src/services/dashboardApi.js
const BASE_URL = 'http://localhost:8083/api';

export const dashboardApi = {
  async getCategoriesForDashboard() {
    const response = await fetch(`${BASE_URL}/categories/admin/all`);
    return response.json();
  },
  
  async createCategory(categoryData) {
    const response = await fetch(`${BASE_URL}/categories`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryData)
    });
    return response.json();
  },
  
  // ... similar methods for other operations
};
```

## Testing

1. **Restart Products Management Service** (port 8083)
2. **Test endpoints directly:**
   ```bash
   curl http://localhost:8083/api/categories/admin/all
   curl "http://localhost:8083/api/products/admin/all?page=0&size=10"
   ```
3. **Update your dashboard code** with the new API calls
4. **Test dashboard operations** (create, read, update, delete)
5. **Verify data consistency** between Postman and Dashboard

## Important Notes

- **Admin Service** should only be used for user management and reports
- **Products Service** should be used for all product and category operations
- **User Service** should be used for authentication and user profiles
- All services are currently configured without authentication (security disabled for testing)
- JWT authentication can be re-enabled when ready for production

## Troubleshooting

**If dashboard still doesn't show data:**
1. Check browser console for API call errors
2. Verify Products Service is running on port 8083
3. Test endpoints directly with curl/Postman
4. Check CORS configuration in Products Service
5. Ensure dashboard is calling correct endpoints (8083 not 8082)
