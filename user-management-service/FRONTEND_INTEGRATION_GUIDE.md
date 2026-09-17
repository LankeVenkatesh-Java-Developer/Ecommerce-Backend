# Frontend Integration Guide for User Management Service

## Backend Service Information

- **Service Name**: User Management Service
- **Base URL**: `http://localhost:8081`
- **API Base Path**: `/api/v1`
- **Full API Base URL**: `http://localhost:8081/api/v1`

## Available API Endpoints

### Authentication Endpoints (Public)

#### Forget Password (Request OTP)
- **Endpoint**: `POST /api/v1/auth/forget-password` or `POST /api/v1/auth/forgot-password`
- **Description**: Request OTP for password reset
- **Request Body**:
```json
{
  "email": "user@example.com"
}
```
- **Response**: "OTP has been sent to your email address"

#### Verify OTP
- **Endpoint**: `POST /api/v1/auth/verify-otp`
- **Description**: Verify the OTP received via email
- **Request Body**:
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```
- **Response**: `true` or `false`

#### Reset Password
- **Endpoint**: `POST /api/v1/auth/reset-password`
- **Description**: Reset password using verified OTP
- **Request Body**:
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewSecurePassword123"
}
```
- **Response**: "Password has been reset successfully"

#### Register User
- **Endpoint**: `POST /api/v1/auth/register`
- **Description**: Register a new user
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "mobileNumber": "9876543210",
  "password": "Password@123",
  "role": "CUSTOMER"
}
```
- **Response**: User object with ID and details

#### Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Description**: Authenticate user and get JWT token
- **Request Body**:
```json
{
  "emailOrMobile": "john.doe@example.com",
  "password": "Password@123"
}
```
- **Response**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "john.doe@example.com",
  "role": "CUSTOMER"
}
```

### User Management Endpoints (Require JWT Token)

#### Get User by ID
- **Endpoint**: `GET /api/v1/users/{id}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: User details

#### Get All Users (Admin Only)
- **Endpoint**: `GET /api/v1/users`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: List of all users

#### Update User
- **Endpoint**: `PUT /api/v1/users/{id}`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**: Same as register (all fields optional)
- **Response**: Updated user details

#### Update User Status (Admin Only)
- **Endpoint**: `PATCH /api/v1/users/{id}/status?status=ACTIVE`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Updated user details

#### Update User Role (Super Admin Only)
- **Endpoint**: `PATCH /api/v1/users/{id}/role?role=ADMIN`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Updated user details

#### Delete User (Super Admin Only)
- **Endpoint**: `DELETE /api/v1/users/{id}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: 204 No Content

### Address Management Endpoints (Require JWT Token)

#### Create Address
- **Endpoint**: `POST /api/v1/addresses/users/{userId}`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**:
```json
{
  "addressLine1": "123 Main Street",
  "addressLine2": "Apt 4B",
  "city": "Mumbai",
  "state": "Maharashtra",
  "country": "India",
  "postalCode": "400001",
  "addressType": "HOME",
  "isDefault": true
}
```
- **Response**: Created address details

#### Get All Addresses for User
- **Endpoint**: `GET /api/v1/addresses/users/{userId}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: List of user's addresses

#### Get Default Address
- **Endpoint**: `GET /api/v1/addresses/users/{userId}/default`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Default address details

#### Update Address
- **Endpoint**: `PUT /api/v1/addresses/{id}/users/{userId}`
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**: Same as create (all fields optional)
- **Response**: Updated address details

#### Set Default Address
- **Endpoint**: `PATCH /api/v1/addresses/{id}/users/{userId}/default`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Updated address details

#### Delete Address
- **Endpoint**: `DELETE /api/v1/addresses/{id}/users/{userId}`
- **Headers**: `Authorization: Bearer {token}`
- **Response**: 204 No Content

## Frontend Integration Steps

### 1. Configure CORS
The backend is already configured to allow CORS from any origin. No additional configuration needed.

### 2. Set Up API Client
Create an API service in your frontend to handle HTTP requests:

```javascript
// Example using fetch API
const API_BASE_URL = 'http://localhost:8081/api/v1';

class UserManagementService {
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

  async register(userData) {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(userData)
    });
    return response.json();
  }

  async login(emailOrMobile, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ emailOrMobile, password })
    });
    const data = await response.json();
    if (data.token) {
      this.setToken(data.token);
    }
    return data;
  }

  async forgetPassword(email) {
    const response = await fetch(`${API_BASE_URL}/auth/forget-password`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ email })
    });
    return response.text();
  }

  async verifyOtp(email, otp) {
    const response = await fetch(`${API_BASE_URL}/auth/verify-otp`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ email, otp })
    });
    return response.json();
  }

  async resetPassword(email, otp, newPassword) {
    const response = await fetch(`${API_BASE_URL}/auth/reset-password`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ email, otp, newPassword })
    });
    return response.text();
  }

  async getUserById(userId) {
    const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
      method: 'GET',
      headers: this.getHeaders()
    });
    return response.json();
  }

  async createAddress(userId, addressData) {
    const response = await fetch(`${API_BASE_URL}/addresses/users/${userId}`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(addressData)
    });
    return response.json();
  }

  // Add more methods as needed...
}

export default new UserManagementService();
```

### 3. Authentication Flow

#### Registration
```javascript
const handleRegister = async (userData) => {
  try {
    const response = await userManagementService.register(userData);
    console.log('Registration successful:', response);
    // Redirect to login page
  } catch (error) {
    console.error('Registration failed:', error);
  }
};
```

#### Login
```javascript
const handleLogin = async (emailOrMobile, password) => {
  try {
    const response = await userManagementService.login(emailOrMobile, password);
    console.log('Login successful:', response);
    // Store user info and redirect to dashboard
    localStorage.setItem('userId', response.userId);
    localStorage.setItem('userRole', response.role);
  } catch (error) {
    console.error('Login failed:', error);
  }
};
```

#### Logout
```javascript
const handleLogout = () => {
  userManagementService.clearToken();
  localStorage.removeItem('userId');
  localStorage.removeItem('userRole');
  // Redirect to login page
};
```

#### Forget Password Flow
```javascript
// Step 1: Request OTP
const handleForgetPassword = async (email) => {
  try {
    const message = await userManagementService.forgetPassword(email);
    console.log(message);
    // Show OTP input field
    setShowOtpInput(true);
  } catch (error) {
    console.error('Failed to send OTP:', error);
  }
};

// Step 2: Verify OTP
const handleVerifyOtp = async (email, otp) => {
  try {
    const isValid = await userManagementService.verifyOtp(email, otp);
    if (isValid) {
      // Show new password input field
      setShowPasswordInput(true);
    } else {
      alert('Invalid OTP');
    }
  } catch (error) {
    console.error('OTP verification failed:', error);
  }
};

// Step 3: Reset Password
const handleResetPassword = async (email, otp, newPassword) => {
  try {
    const message = await userManagementService.resetPassword(email, otp, newPassword);
    console.log(message);
    // Redirect to login page
    alert('Password reset successful. Please login with your new password.');
    window.location.href = '/login';
  } catch (error) {
    console.error('Password reset failed:', error);
  }
};
```

### 4. React Component Example for Password Reset

Here's a complete React component implementing the password reset flow:

```jsx
import React, { useState } from 'react';
import userManagementService from './userManagementService';

const ForgetPasswordPage = () => {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [step, setStep] = useState(1); // 1: email, 2: otp, 3: new password
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const handleRequestOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await userManagementService.forgetPassword(email);
      setMessage(response);
      setStep(2);
    } catch (err) {
      setError('Failed to send OTP. Please check your email and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const isValid = await userManagementService.verifyOtp(email, otp);
      if (isValid) {
        setStep(3);
        setMessage('OTP verified successfully');
      } else {
        setError('Invalid OTP. Please try again.');
      }
    } catch (err) {
      setError('OTP verification failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    if (newPassword.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      const response = await userManagementService.resetPassword(email, otp, newPassword);
      setMessage(response);
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
    } catch (err) {
      setError('Password reset failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forget-password-container">
      <h2>Reset Password</h2>
      
      {error && <div className="error-message">{error}</div>}
      {message && <div className="success-message">{message}</div>}
      
      {step === 1 && (
        <form onSubmit={handleRequestOtp}>
          <div className="form-group">
            <label>Email Address:</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="Enter your registered email"
            />
          </div>
          <button type="submit" disabled={loading}>
            {loading ? 'Sending...' : 'Send OTP'}
          </button>
        </form>
      )}
      
      {step === 2 && (
        <form onSubmit={handleVerifyOtp}>
          <div className="form-group">
            <label>Enter OTP:</label>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
              maxLength={6}
              placeholder="Enter 6-digit OTP"
            />
          </div>
          <button type="submit" disabled={loading}>
            {loading ? 'Verifying...' : 'Verify OTP'}
          </button>
          <button type="button" onClick={() => setStep(1)}>
            Back
          </button>
        </form>
      )}
      
      {step === 3 && (
        <form onSubmit={handleResetPassword}>
          <div className="form-group">
            <label>New Password:</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
              placeholder="Enter new password (min 8 characters)"
            />
          </div>
          <div className="form-group">
            <label>Confirm Password:</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              placeholder="Confirm new password"
            />
          </div>
          <button type="submit" disabled={loading}>
            {loading ? 'Resetting...' : 'Reset Password'}
          </button>
          <button type="button" onClick={() => setStep(2)}>
            Back
          </button>
        </form>
      )}
    </div>
  );
};

export default ForgetPasswordPage;
```

### 5. Protected Routes
Implement route guards in your frontend to check for JWT token:

```javascript
const requireAuth = (nextState, replace) => {
  const token = localStorage.getItem('jwtToken');
  if (!token) {
    replace('/login');
  }
};
```

### 5. Error Handling
Implement proper error handling for API responses:

```javascript
const handleApiCall = async (apiCall) => {
  try {
    const response = await apiCall();
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'API call failed');
    }
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    // Show error message to user
    throw error;
  }
};
```

## Testing the Integration

### 1. Start the Backend
```bash
cd C:\Workspace\EcommerceProject\EcomerceBackEnd\user-management-service
mvn spring-boot:run
```

### 2. Verify Backend is Running
Open browser and visit: `http://localhost:8081`

### 3. Test Registration
Use Postman or curl:
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "mobileNumber": "9876543210",
    "password": "Test@1234"
  }'
```

### 4. Test Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrMobile": "test@example.com",
    "password": "Test@1234"
  }'
```

### 5. Test Protected Endpoints
Use the token from login response:
```bash
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Validation Rules

### Email Validation
- Must be valid email format
- Must be unique across all users

### Mobile Number Validation
- Must be 10 digits
- Must start with 6-9 (Indian format)
- Must be unique across all users

### Password Validation
- Minimum 8 characters
- Must contain at least one uppercase letter
- Must contain at least one lowercase letter
- Must contain at least one digit
- Must contain at least one special character

## Common Issues and Solutions

### CORS Issues
- If you encounter CORS errors, check that the frontend URL is allowed
- The backend is configured to allow all origins for development

### Token Expiration
- JWT tokens expire after 24 hours (86400000 ms)
- Implement token refresh logic or re-authentication

### Database Connection
- Ensure MySQL is running on localhost:3306
- Verify database credentials in application.properties
- The database will be created automatically if it doesn't exist

## Security Considerations

1. **Always use HTTPS in production**
2. **Store JWT tokens securely** (consider httpOnly cookies)
3. **Implement token refresh mechanism**
4. **Validate all user inputs on both frontend and backend**
5. **Rate limit authentication endpoints**
6. **Log security events**

## Support

For issues or questions:
- Check backend logs: `logs/application.log`
- Verify database connection
- Check JWT token validity
- Review API response messages for specific errors
