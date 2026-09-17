# Forget Password Feature Setup

## Overview
The forget password feature has been implemented with the following functionality:
- Users can request a password reset by providing their email
- A 6-digit OTP is generated and sent to the user's email
- Users can verify the OTP
- Users can reset their password using the verified OTP

## API Endpoints

### 1. Forget Password (Request OTP)
**Endpoint:** `POST /api/v1/auth/forget-password`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
"OTP has been sent to your email address"
```

### 2. Verify OTP
**Endpoint:** `POST /api/v1/auth/verify-otp`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response:**
```json
true
```

### 3. Reset Password
**Endpoint:** `POST /api/v1/auth/reset-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewSecurePassword123"
}
```

**Response:**
```json
"Password has been reset successfully"
```

## Email Configuration

To enable email sending, you need to configure your email credentials in `application.properties` or as environment variables.

### For Gmail:
1. Enable 2-Factor Authentication on your Google Account
2. Generate an App Password:
   - Go to Google Account Settings > Security
   - Select "2-Step Verification"
   - Select "App passwords"
   - Generate a new app password
3. Update the configuration:

**Option 1: Using environment variables (Recommended)**
```bash
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
```

**Option 2: Directly in application.properties**
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### For other email providers:
Update the following properties in `application.properties`:
```properties
spring.mail.host=smtp.your-provider.com
spring.mail.port=587
spring.mail.username=your-email@provider.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## OTP Details
- **Length:** 6 digits
- **Expiry:** 10 minutes
- **Security:** OTPs are marked as used after successful password reset
- **Storage:** OTPs are stored in the database with proper indexing

## Database Schema
A new table `otps` will be created automatically with the following columns:
- `id` (Primary Key)
- `email` (Indexed)
- `otp_code` (Indexed)
- `expires_at` (Indexed)
- `is_used` (Boolean)
- `created_at` (Timestamp)

## Testing the Feature

1. Start the application
2. Ensure email configuration is set up
3. Use a REST client (Postman, curl, etc.) to test the endpoints
4. Check your email for the OTP
5. Verify the OTP and reset the password

## Security Notes
- All forget password endpoints are publicly accessible (no authentication required)
- OTPs expire after 10 minutes
- Each new OTP request invalidates previous OTPs for that email
- OTPs can only be used once
- Passwords are encrypted using BCrypt before storage
