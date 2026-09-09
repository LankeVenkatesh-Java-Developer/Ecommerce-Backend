# Frontend Integration Guide - Products Management Service

## Backend API Information

**Base URL:** `http://localhost:8083`

**Available Endpoints:**

### Categories API
- `GET /api/categories` - Get all active categories (for home page)
- `GET /api/categories/admin/all` - Get all categories including inactive (for dashboard)
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Products API
- `GET /api/products` - Get products with pagination, filtering, and search (for home page)
- `GET /api/products/admin/all` - Get all products without status filtering (for dashboard)
- `GET /api/products/{id}` - Get product details (master view)
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `PATCH /api/products/{id}/status?status={status}` - Update product status

## Query Parameters for Product Listing

- `page` (default: 0) - Page number
- `size` (default: 10) - Items per page (1-100)
- `sort` (default: name,asc) - Sort format: field,direction
- `categoryId` - Filter by category ID
- `search` - Search in name and description (case-insensitive)
- `status` - Filter by status (ACTIVE, INACTIVE, OUT_OF_STOCK)

## Example API Calls

### Get All Categories
```bash
curl http://localhost:8083/api/categories
```

### Get Products with Pagination (Home Page - With Filters)
```bash
curl "http://localhost:8083/api/products?page=0&size=10"
```

### Get All Products (Dashboard - No Status Filtering)
```bash
curl "http://localhost:8083/api/products/admin/all?page=0&size=10"
```

### Filter by Category
```bash
curl "http://localhost:8083/api/products?categoryId=1"
```

### Search Products
```bash
curl "http://localhost:8083/api/products?search=iphone"
```

### Combined Search + Category Filter
```bash
curl "http://localhost:8083/api/products?search=phone&categoryId=1&page=0&size=20"
```

### Get Product Details
```bash
curl http://localhost:8083/api/products/1
```

## React Integration Example

### Setup API Service
```javascript
// src/services/api.js
const BASE_URL = 'http://localhost:8083/api';

export const api = {
  // Categories
  getCategories: async () => {
    const response = await fetch(`${BASE_URL}/categories`);
    return response.json();
  },
  
  getCategoryById: async (id) => {
    const response = await fetch(`${BASE_URL}/categories/${id}`);
    return response.json();
  },
  
  // Products
  getProducts: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const response = await fetch(`${BASE_URL}/products?${queryString}`);
    return response.json();
  },
  
  getProductById: async (id) => {
    const response = await fetch(`${BASE_URL}/products/${id}`);
    return response.json();
  },
  
  // Admin operations (will need authentication)
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
    return response.json();
  }
};
```

### Product Listing Component
```javascript
// src/components/ProductList.jsx
import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    categoryId: null,
    search: '',
    status: null
  });

  // Fetch categories on mount
  useEffect(() => {
    api.getCategories().then(setCategories);
  }, []);

  // Fetch products when filters change
  useEffect(() => {
    setLoading(true);
    api.getProducts(filters).then(data => {
      setProducts(data);
      setLoading(false);
    });
  }, [filters]);

  const handleSearch = (e) => {
    setFilters({ ...filters, search: e.target.value, page: 0 });
  };

  const handleCategoryFilter = (categoryId) => {
    setFilters({ ...filters, categoryId, page: 0 });
  };

  const handlePageChange = (newPage) => {
    setFilters({ ...filters, page: newPage });
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      {/* Search Bar */}
      <input
        type="text"
        placeholder="Search products..."
        value={filters.search}
        onChange={handleSearch}
      />

      {/* Category Filter */}
      <select onChange={(e) => handleCategoryFilter(e.target.value || null)}>
        <option value="">All Categories</option>
        {categories.map(cat => (
          <option key={cat.id} value={cat.id}>{cat.name}</option>
        ))}
      </select>

      {/* Product Grid */}
      <div className="product-grid">
        {products.content?.map(product => (
          <div key={product.id} className="product-card">
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p>Price: ₹{product.price}</p>
            <p>Category: {product.categoryName}</p>
            <p>Status: {product.status}</p>
          </div>
        ))}
      </div>

      {/* Pagination */}
      <div className="pagination">
        <button 
          disabled={products.pageable?.pageNumber === 0}
          onClick={() => handlePageChange(filters.page - 1)}
        >
          Previous
        </button>
        <span>Page {products.pageable?.pageNumber + 1} of {products.totalPages}</span>
        <button 
          disabled={products.pageable?.pageNumber >= products.totalPages - 1}
          onClick={() => handlePageChange(filters.page + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default ProductList;
```

### Product Details Component
```javascript
// src/components/ProductDetails.jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../services/api';

const ProductDetails = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.getProductById(id).then(data => {
      setProduct(data);
      setLoading(false);
    });
  }, [id]);

  if (loading) return <div>Loading...</div>;
  if (!product) return <div>Product not found</div>;

  return (
    <div className="product-details">
      <h1>{product.name}</h1>
      <p>{product.description}</p>
      <p><strong>Price:</strong> ₹{product.price}</p>
      <p><strong>Quantity:</strong> {product.quantity}</p>
      <p><strong>Status:</strong> {product.status}</p>
      
      {product.category && (
        <div>
          <h3>Category</h3>
          <p>{product.category.name}</p>
          <p>{product.category.description}</p>
        </div>
      )}
      
      <p><strong>Created:</strong> {new Date(product.createdAt).toLocaleString()}</p>
      <p><strong>Updated:</strong> {new Date(product.updatedAt).toLocaleString()}</p>
    </div>
  );
};

export default ProductDetails;
```

## Vue.js Integration Example

### API Service
```javascript
// src/services/api.js
const BASE_URL = 'http://localhost:8083/api';

export const api = {
  async getCategories() {
    const response = await fetch(`${BASE_URL}/categories`);
    return response.json();
  },
  
  async getProducts(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const response = await fetch(`${BASE_URL}/products?${queryString}`);
    return response.json();
  },
  
  async getProductById(id) {
    const response = await fetch(`${BASE_URL}/products/${id}`);
    return response.json();
  }
};
```

### Product List Component
```vue
<!-- src/components/ProductList.vue -->
<template>
  <div>
    <input 
      v-model="filters.search" 
      placeholder="Search products..."
      @input="fetchProducts"
    />
    
    <select v-model="filters.categoryId" @change="fetchProducts">
      <option :value="null">All Categories</option>
      <option v-for="cat in categories" :key="cat.id" :value="cat.id">
        {{ cat.name }}
      </option>
    </select>

    <div v-if="loading">Loading...</div>
    
    <div class="product-grid">
      <div v-for="product in products.content" :key="product.id" class="product-card">
        <h3>{{ product.name }}</h3>
        <p>{{ product.description }}</p>
        <p>Price: ₹{{ product.price }}</p>
        <p>Category: {{ product.categoryName }}</p>
      </div>
    </div>

    <div class="pagination">
      <button 
        :disabled="products.pageable?.pageNumber === 0"
        @click="changePage(filters.page - 1)"
      >
        Previous
      </button>
      <span>Page {{ products.pageable?.pageNumber + 1 }} of {{ products.totalPages }}</span>
      <button 
        :disabled="products.pageable?.pageNumber >= products.totalPages - 1"
        @click="changePage(filters.page + 1)"
      >
        Next
      </button>
    </div>
  </div>
</template>

<script>
import { api } from '../services/api';

export default {
  data() {
    return {
      products: {},
      categories: [],
      loading: true,
      filters: {
        page: 0,
        size: 10,
        categoryId: null,
        search: ''
      }
    };
  },
  
  async mounted() {
    await this.fetchCategories();
    await this.fetchProducts();
  },
  
  methods: {
    async fetchCategories() {
      this.categories = await api.getCategories();
    },
    
    async fetchProducts() {
      this.loading = true;
      this.products = await api.getProducts(this.filters);
      this.loading = false;
    },
    
    changePage(newPage) {
      this.filters.page = newPage;
      this.fetchProducts();
    }
  }
};
</script>
```

## Angular Integration Example

### API Service
```typescript
// src/app/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8083/api';

  constructor(private http: HttpClient) {}

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/categories`);
  }

  getProducts(params: any = {}): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/products`, { params });
  }

  getProductById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/products/${id}`);
  }
}
```

### Product List Component
```typescript
// src/app/components/product-list/product-list.component.ts
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-product-list',
  template: `
    <div>
      <input [(ngModel)]="filters.search" (ngModelChange)="fetchProducts()" placeholder="Search...">
      
      <select [(ngModel)]="filters.categoryId" (ngModelChange)="fetchProducts()">
        <option [ngValue]="null">All Categories</option>
        <option *ngFor="let cat of categories" [ngValue]="cat.id">
          {{ cat.name }}
        </option>
      </select>

      <div *ngIf="loading">Loading...</div>
      
      <div class="product-grid">
        <div *ngFor="let product of products.content" class="product-card">
          <h3>{{ product.name }}</h3>
          <p>{{ product.description }}</p>
          <p>Price: ₹{{ product.price }}</p>
        </div>
      </div>

      <div class="pagination">
        <button [disabled]="products.pageable?.pageNumber === 0" (click)="changePage(filters.page - 1)">
          Previous
        </button>
        <span>Page {{ products.pageable?.pageNumber + 1 }} of {{ products.totalPages }}</span>
        <button [disabled]="products.pageable?.pageNumber >= products.totalPages - 1" (click)="changePage(filters.page + 1)">
          Next
        </button>
      </div>
    </div>
  `
})
export class ProductListComponent implements OnInit {
  products: any = {};
  categories: any[] = [];
  loading = true;
  filters = {
    page: 0,
    size: 10,
    categoryId: null as number | null,
    search: ''
  };

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.fetchCategories();
    this.fetchProducts();
  }

  fetchCategories() {
    this.apiService.getCategories().subscribe(data => {
      this.categories = data;
    });
  }

  fetchProducts() {
    this.loading = true;
    this.apiService.getProducts(this.filters).subscribe(data => {
      this.products = data;
      this.loading = false;
    });
  }

  changePage(newPage: number) {
    this.filters.page = newPage;
    this.fetchProducts();
  }
}
```

## CORS Configuration

The backend is already configured to allow CORS from any origin. If you need to restrict to specific origins, update `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://your-frontend-domain.com"));
```

## Error Handling

### Response Format for Errors
```json
{
  "status": 404,
  "message": "Product not found with id: 101",
  "timestamp": "2026-08-30T10:30:00",
  "path": "/api/products/101"
}
```

### Error Handling Example (React)
```javascript
try {
  const product = await api.getProductById(id);
  setProduct(product);
} catch (error) {
  if (error.status === 404) {
    setError('Product not found');
  } else {
    setError('An error occurred');
  }
}
```

## Testing the Connection

### Test with Browser
1. Start the backend: `mvn spring-boot:run`
2. Open browser: `http://localhost:8083/api/categories`
3. You should see JSON response with categories

### Test with Frontend
1. Start your frontend development server
2. Make API calls to `http://localhost:8083/api/*`
3. Check browser console for CORS errors (should be none)

## Common Issues

### CORS Errors
If you see CORS errors in browser console:
- Verify backend is running on port 8083
- Check SecurityConfig.java has CORS enabled
- Ensure frontend is making requests to correct URL

### Connection Refused
- Verify MySQL is running
- Check database credentials in application.properties
- Ensure database `ecommerce_products` exists or will be created automatically

### 404 Errors
- Verify endpoint URL is correct
- Check if resource exists in database
- Use the correct HTTP method (GET, POST, etc.)

## Next Steps

1. **Choose your frontend framework** (React, Vue, Angular, or vanilla JS)
2. **Set up API service** using the examples above
3. **Create components** for product listing and product details
4. **Implement pagination** and filtering
5. **Add error handling** for API failures
6. **Style the UI** with CSS or a UI framework
7. **Test thoroughly** with various scenarios

## Authentication (Future)

Currently, GET endpoints are public. To implement authentication for admin operations:
1. Add JWT/OAuth2 dependency to backend
2. Configure authentication provider in SecurityConfig.java
3. Add JWT token to frontend API calls:
```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```
