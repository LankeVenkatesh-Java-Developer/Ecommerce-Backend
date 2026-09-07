# Deployment Guide - Render (Free Hosting)

## Overview
This microservice can be deployed independently on Render using their free tier (no credit card required).

## Prerequisites
- GitHub account
- Render account (free signup at https://render.com)

## Deployment Steps

### 1. Push Code to GitHub
```bash
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

### 2. Deploy on Render

1. Go to https://dashboard.render.com
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Render will automatically detect the `render.yaml` file
5. Click "Deploy Web Service"

### 3. Database Setup
The `render.yaml` automatically creates a free PostgreSQL database:
- Database name: `user-management-db`
- Connection details are automatically injected as environment variables

### 4. Access Your Service
After deployment, Render will provide:
- Service URL: `https://user-management-service.onrender.com`
- API endpoint: `https://user-management-service.onrender.com/api/v1`

## Environment Variables
The following are automatically configured by Render:
- `SPRING_DATASOURCE_URL` - PostgreSQL connection string
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `APP_JWT_SECRET` - Auto-generated JWT secret

## Local Development (MySQL)
For local development, the application uses MySQL:
- Database: `user_management_db`
- Port: 3306
- See `application.properties` for configuration

## Production (PostgreSQL)
For production on Render, the application uses PostgreSQL:
- Profile: `prod`
- See `application-prod.properties` for configuration

## Deploying Other Microservices
Each microservice needs its own:
1. `render.yaml` file with unique service name
2. PostgreSQL database (if needed)
3. Separate GitHub repository or subdirectory

Example for another service:
```yaml
services:
  - type: web
    name: product-service  # Change this
    env: java
    plan: free
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/product-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
    envVars:
      - key: SPRING_DATASOURCE_URL
        fromDatabase:
          name: product-db  # Change this
          property: connectionString
      # ... other env vars

databases:
  - name: product-db  # Change this
    databaseName: product_db
    user: product_user
    plan: free
```

## Important Notes
- Free tier services spin down after 15 minutes of inactivity
- Cold start takes ~30-60 seconds
- Database is always available (doesn't spin down)
- Each microservice is deployed independently
