# Frontend Integration Guide for Admin Management Service

## Backend Service Information

- **Service Name**: Admin Management Service
- **Base URL**: `http://localhost:8082`
- **API Base Path**: `/api/admin`
- **Full API Base URL**: `http://localhost:8082/api/admin`

## Available API Endpoints

### Authentication Requirement

All Admin Management Service endpoints require JWT authentication. You must first obtain a JWT token from the **User Management Service** login endpoint.

### Step 1: Obtain JWT Token from User Management Service

```http
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "emailOrMobile": "admin@example.com",
  "password": "Admin@123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### Step 2: Use JWT Token for Admin Management Service

Include the JWT token in the Authorization header for all Admin Management Service requests:

```
Authorization: Bearer {JWT_TOKEN}
```

---

## Category Management Endpoints

### Create Category
```http
POST http://localhost:8082/api/admin/categories
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "status": "ACTIVE"
}
```

**Response (201 CREATED):**
```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:30:00"
}
```

### Get Category by ID
```http
GET http://localhost:8082/api/admin/categories/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:30:00"
}
```

### Get All Categories (Paginated)
```http
GET http://localhost:8082/api/admin/categories?page=0&size=10&sortBy=id&sortDir=ASC
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Electronics",
      "description": "Electronic devices and accessories",
      "status": "ACTIVE",
      "createdAt": "2026-08-28T13:30:00",
      "updatedAt": "2026-08-28T13:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Search Categories
```http
GET http://localhost:8082/api/admin/categories/search?name=Electronics&status=ACTIVE&page=0&size=10
Authorization: Bearer {JWT_TOKEN}
```

**Query Parameters:**
- `name` (optional): Category name to search for (partial match)
- `status` (optional): Filter by status (ACTIVE, INACTIVE)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: id)
- `sortDir` (optional): Sort direction (ASC, DESC, default: ASC)

### Update Category
```http
PUT http://localhost:8082/api/admin/categories/1
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Electronics Updated",
  "description": "Updated description",
  "status": "ACTIVE"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Electronics Updated",
  "description": "Updated description",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:35:00"
}
```

### Update Category Status
```http
PATCH http://localhost:8082/api/admin/categories/1/status?status=INACTIVE
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "status": "INACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:35:00"
}
```

### Delete Category
```http
DELETE http://localhost:8082/api/admin/categories/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (204 NO CONTENT)**

**Note:** Categories with existing products cannot be deleted.

---

## Product Management Endpoints

### Create Product
```http
POST http://localhost:8082/api/admin/products
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

**Response (201 CREATED):**
```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "Electronics",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:30:00"
}
```

### Get Product by ID
```http
GET http://localhost:8082/api/admin/products/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "Electronics",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:30:00"
}
```

### Get All Products (Paginated)
```http
GET http://localhost:8082/api/admin/products?page=0&size=20&sortBy=id&sortDir=ASC
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "categoryId": 1,
      "categoryName": "Electronics",
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "stockQuantity": 50,
      "sku": "LAPTOP-001",
      "brand": "Dell",
      "status": "ACTIVE",
      "createdAt": "2026-08-28T13:30:00",
      "updatedAt": "2026-08-28T13:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Search Products
```http
GET http://localhost:8082/api/admin/products/search?name=Laptop&categoryId=1&brand=Dell&status=ACTIVE&minPrice=500&maxPrice=1500&page=0&size=20
Authorization: Bearer {JWT_TOKEN}
```

**Query Parameters:**
- `name` (optional): Product name to search for (partial match)
- `categoryId` (optional): Filter by category ID
- `brand` (optional): Filter by brand (partial match)
- `status` (optional): Filter by status (ACTIVE, INACTIVE, OUT_OF_STOCK)
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sortBy` (optional): Sort field (default: id)
- `sortDir` (optional): Sort direction (ASC, DESC, default: ASC)

### Update Product
```http
PUT http://localhost:8082/api/admin/products/1
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

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "Electronics",
  "name": "Laptop Updated",
  "description": "Updated description",
  "price": 1099.99,
  "stockQuantity": 45,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:35:00"
}
```

### Update Product Status
```http
PATCH http://localhost:8082/api/admin/products/1/status?status=INACTIVE
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "Electronics",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "INACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:35:00"
}
```

### Update Stock Quantity
```http
PATCH http://localhost:8082/api/admin/products/1/stock?quantity=100
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "Electronics",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 100,
  "sku": "LAPTOP-001",
  "brand": "Dell",
  "status": "ACTIVE",
  "createdAt": "2026-08-28T13:30:00",
  "updatedAt": "2026-08-28T13:35:00"
}
```

**Note:** When stock quantity reaches 0, status automatically changes to OUT_OF_STOCK.

### Delete Product
```http
DELETE http://localhost:8082/api/admin/products/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (204 NO CONTENT)**

---

## Report Management Endpoints

### Generate Product Report (Excel)
```http
GET http://localhost:8082/api/admin/reports/products/excel
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=products_report_20260828_133000.xlsx`

### Generate Category Report (Excel)
```http
GET http://localhost:8082/api/admin/reports/categories/excel
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=categories_report_20260828_133000.xlsx`

### Generate Stock Report (Excel)
```http
GET http://localhost:8082/api/admin/reports/stock/excel
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=stock_report_20260828_133000.xlsx`

---

## Frontend Integration Steps

### 1. Configure CORS
The backend is already configured to allow CORS from any origin. No additional configuration needed.

### 2. Set Up API Client

Create an API service in your frontend to handle HTTP requests:

```javascript
// Example using fetch API
const ADMIN_API_BASE_URL = 'http://localhost:8082/api/admin';
const USER_API_BASE_URL = 'http://localhost:8081/api/v1';

class AdminManagementService {
  constructor() {
    this.token = localStorage.getItem('jwtToken');
  }

  setToken(token) {
    this.token = token;
    localStorage.setItem('jwtToken', token);
  }

  clearToken() {
    this.token = null;
    localStorage.removeItem('jwtToken');
  }

  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }
    
    return headers;
  }

  // Login via User Management Service
  async login(emailOrMobile, password) {
    const response = await fetch(`${USER_API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ emailOrMobile, password })
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }
    const data = await response.json();
    if (data.token) {
      this.setToken(data.token);
    }
    return data;
  }

  // Category Methods
  async createCategory(categoryData) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(categoryData)
    });
    return response.json();
  }

  async getCategories(page = 0, size = 10, sortBy = 'id', sortDir = 'ASC') {
    const response = await fetch(
      `${ADMIN_API_BASE_URL}/categories?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`,
      {
        method: 'GET',
        headers: this.getHeaders()
      }
    );
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch categories');
    }
    return response.json();
  }

  async getCategoryById(id) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories/${id}`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch category');
    }
    return response.json();
  }

  async searchCategories(filters) {
    const params = new URLSearchParams(filters).toString();
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories/search?${params}`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to search categories');
    }
    return response.json();
  }

  async updateCategory(id, categoryData) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories/${id}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(categoryData)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update category');
    }
    return response.json();
  }

  async updateCategoryStatus(id, status) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories/${id}/status?status=${status}`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update category status');
    }
    return response.json();
  }

  async deleteCategory(id) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/categories/${id}`, {
      method: 'DELETE',
      headers: this.getHeaders()
    });
    if (!response.ok && response.status !== 204) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete category');
    }
    return response;
  }

  // Product Methods
  async createProduct(productData) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(productData)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create product');
    }
    return response.json();
  }

  async getProducts(page = 0, size = 20, sortBy = 'id', sortDir = 'ASC') {
    const response = await fetch(
      `${ADMIN_API_BASE_URL}/products?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`,
      {
        method: 'GET',
        headers: this.getHeaders()
      }
    );
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch products');
    }
    return response.json();
  }

  async getProductById(id) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/${id}`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch product');
    }
    return response.json();
  }

  async searchProducts(filters) {
    const params = new URLSearchParams(filters).toString();
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/search?${params}`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to search products');
    }
    return response.json();
  }

  async updateProduct(id, productData) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/${id}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(productData)
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update product');
    }
    return response.json();
  }

  async updateProductStatus(id, status) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/${id}/status?status=${status}`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update product status');
    }
    return response.json();
  }

  async updateProductStock(id, quantity) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/${id}/stock?quantity=${quantity}`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update product stock');
    }
    return response.json();
  }

  async deleteProduct(id) {
    const response = await fetch(`${ADMIN_API_BASE_URL}/products/${id}`, {
      method: 'DELETE',
      headers: this.getHeaders()
    });
    if (!response.ok && response.status !== 204) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete product');
    }
    return response;
  }

  // Report Methods
  async downloadProductReport() {
    const response = await fetch(`${ADMIN_API_BASE_URL}/reports/products/excel`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to download product report');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `products_report_${new Date().getTime()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    return response;
  }

  async downloadCategoryReport() {
    const response = await fetch(`${ADMIN_API_BASE_URL}/reports/categories/excel`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to download category report');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `categories_report_${new Date().getTime()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    return response;
  }

  async downloadStockReport() {
    const response = await fetch(`${ADMIN_API_BASE_URL}/reports/stock/excel`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to download stock report');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `stock_report_${new Date().getTime()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    return response;
  }
}

export default new AdminManagementService();
```

### 3. Authentication Flow

#### Login
```javascript
const handleLogin = async (emailOrMobile, password) => {
  try {
    const response = await adminManagementService.login(emailOrMobile, password);
    console.log('Login successful:', response);
    // Store user info and redirect to admin dashboard
    localStorage.setItem('userId', response.userId);
    localStorage.setItem('userRole', response.role);
    
    // Check if user has admin role
    if (response.role !== 'ADMIN' && response.role !== 'SUPER_ADMIN') {
      alert('Access denied. Admin role required.');
      adminManagementService.clearToken();
      return;
    }
    
    // Redirect to admin dashboard
    window.location.href = '/admin/dashboard';
  } catch (error) {
    console.error('Login failed:', error);
    alert('Login failed. Please check your credentials.');
  }
};
```

#### Logout
```javascript
const handleLogout = () => {
  adminManagementService.clearToken();
  localStorage.removeItem('userId');
  localStorage.removeItem('userRole');
  // Redirect to login page
  window.location.href = '/login';
};
```

### 4. Protected Routes
Implement route guards in your frontend to check for JWT token and admin role:

```javascript
const requireAdminAuth = (nextState, replace) => {
  const token = localStorage.getItem('jwtToken');
  const userRole = localStorage.getItem('userRole');
  
  if (!token) {
    replace('/login');
    return;
  }
  
  if (userRole !== 'ADMIN' && userRole !== 'SUPER_ADMIN') {
    replace('/unauthorized');
    return;
  }
};
```

### 5. Error Handling
The API service class now includes built-in error handling. For additional error handling in your components:

```javascript
const handleApiError = (error) => {
  console.error('API Error:', error);
  
  if (error.message.includes('401') || error.message.includes('Unauthorized')) {
    // Token expired or invalid
    handleLogout();
  } else if (error.message.includes('403') || error.message.includes('Forbidden')) {
    // Insufficient permissions
    alert('Access denied. You don\'t have permission to perform this action.');
  } else if (error.message.includes('404') || error.message.includes('not found')) {
    // Resource not found
    alert('Resource not found.');
  } else if (error.message.includes('409') || error.message.includes('already exists')) {
    // Duplicate resource
    alert('This resource already exists.');
  } else {
    alert('An error occurred. Please try again.');
  }
};

// Usage in components:
try {
  await adminManagementService.createCategory(categoryData);
  // Success handling
} catch (error) {
  handleApiError(error);
}
```

### 6. Example: Category Management Component

```javascript
import React, { useState, useEffect } from 'react';
import adminManagementService from '../services/adminManagementService';

const CategoryManagement = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadCategories();
  }, [page]);

  const loadCategories = async () => {
    setLoading(true);
    try {
      const response = await adminManagementService.getCategories(page, 10);
      setCategories(response.content);
      setTotalPages(response.pageable.totalPages);
    } catch (error) {
      console.error('Failed to load categories:', error);
      alert('Failed to load categories. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateCategory = async (categoryData) => {
    try {
      await adminManagementService.createCategory(categoryData);
      loadCategories();
      alert('Category created successfully!');
    } catch (error) {
      console.error('Failed to create category:', error);
      alert('Failed to create category: ' + error.message);
    }
  };

  const handleDeleteCategory = async (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await adminManagementService.deleteCategory(id);
        loadCategories();
        alert('Category deleted successfully!');
      } catch (error) {
        console.error('Failed to delete category:', error);
        alert('Failed to delete category: ' + error.message);
      }
    }
  };

  return (
    <div>
      <h2>Category Management</h2>
      
      {/* Create Category Form */}
      <form onSubmit={(e) => {
        e.preventDefault();
        const formData = {
          name: e.target.name.value,
          description: e.target.description.value,
          status: e.target.status.value
        };
        handleCreateCategory(formData);
      }}>
        <input name="name" placeholder="Category Name" required />
        <input name="description" placeholder="Description" />
        <select name="status">
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
        <button type="submit">Create Category</button>
      </form>

      {/* Categories List */}
      {loading ? (
        <p>Loading...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {categories.map(category => (
              <tr key={category.id}>
                <td>{category.id}</td>
                <td>{category.name}</td>
                <td>{category.description}</td>
                <td>{category.status}</td>
                <td>
                  <button onClick={() => handleDeleteCategory(category.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Pagination */}
      <div>
        <button 
          onClick={() => setPage(p => Math.max(0, p - 1))}
          disabled={page === 0}
        >
          Previous
        </button>
        <span>Page {page + 1} of {totalPages}</span>
        <button 
          onClick={() => setPage(p => p + 1)}
          disabled={page >= totalPages - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default CategoryManagement;
```

---

## Testing the Integration

### 1. Start Both Services

**User Management Service:**
```bash
cd C:\Workspace\EcommerceProject\EcomerceBackEnd\user-management-service
mvn spring-boot:run
```

**Admin Management Service:**
```bash
cd C:\Workspace\EcommerceProject\EcomerceBackEnd\Admin-management-service
mvn spring-boot:run
```

### 2. Verify Services are Running

- User Management: http://localhost:8081/api/v1
- Admin Management: http://localhost:8082/api/admin

### 3. Test Authentication

Use Postman or curl to test login:

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrMobile": "admin@example.com",
    "password": "Admin@123"
  }'
```

### 4. Test Admin Endpoints

Use the JWT token from login response:

```bash
# Create Category
curl -X POST http://localhost:8082/api/admin/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices",
    "status": "ACTIVE"
  }'

# Get Categories
curl -X GET http://localhost:8082/api/admin/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create Product
curl -X POST http://localhost:8082/api/admin/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stockQuantity": 50,
    "sku": "LAPTOP-001",
    "brand": "Dell",
    "status": "ACTIVE"
  }'
```

### 5. Test Excel Reports

```bash
# Download Product Report
curl -X GET http://localhost:8082/api/admin/reports/products/excel \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output products_report.xlsx
```

---

## Validation Rules

### Category Validation

- **name**: Required, max 100 characters, must be unique
- **description**: Optional, max 500 characters
- **status**: Optional (default: ACTIVE), must be ACTIVE or INACTIVE

### Product Validation

- **categoryId**: Required, must reference existing active category
- **name**: Required, max 200 characters
- **description**: Optional, max 1000 characters
- **price**: Required, must be > 0, max 8 integer digits, 2 decimal places
- **stockQuantity**: Required, must be >= 0
- **sku**: Required, max 50 characters, must be unique
- **brand**: Optional, max 100 characters
- **status**: Optional (default: ACTIVE), must be ACTIVE, INACTIVE, or OUT_OF_STOCK

---

## Common Issues and Solutions

### CORS Issues
- If you encounter CORS errors, check that the frontend URL is allowed
- The backend is configured to allow all origins for development

### Token Expiration
- JWT tokens expire after 24 hours (86400000 ms)
- Implement token refresh logic or re-authentication

### 401 Unauthorized
- Check that the JWT token is valid and not expired
- Ensure the Authorization header format is correct: `Bearer {token}`

### 403 Forbidden
- Check that the user has ADMIN or SUPER_ADMIN role
- Verify the role is correctly set in the User Management Service

### 404 Not Found
- Verify the resource ID exists
- Check that the category/product hasn't been deleted

### 409 Conflict
- Category name already exists
- Product SKU already exists
- Category has associated products (cannot delete)

### Database Connection
- Ensure MySQL is running on localhost:3306
- Verify database credentials in application.properties
- The database will be created automatically if it doesn't exist

---

## Security Considerations

1. **Always use HTTPS in production**
2. **Store JWT tokens securely** (consider httpOnly cookies)
3. **Implement token refresh mechanism**
4. **Validate all user inputs on both frontend and backend**
5. **Rate limit authentication endpoints**
6. **Log security events**
7. **Never expose sensitive information in logs**
8. **Implement proper session management**

---

## Support

For issues or questions:
- Check backend logs: `logs/application.log`
- Verify database connection
- Check JWT token validity
- Review API response messages for specific errors
- Access Swagger UI: http://localhost:8082/api/admin/swagger-ui.html
