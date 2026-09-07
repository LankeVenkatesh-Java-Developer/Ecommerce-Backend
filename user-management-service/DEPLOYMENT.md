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

### 3. Create PostgreSQL Database

1. In Render dashboard, click "New +" → "PostgreSQL"
2. Name it: `user-management-db`
3. Database name: `user_management_db`
4. User: `user_management_user`
5. Select free plan
6. Click "Create Database"

### 4. Configure Database Environment Variables

1. Go to your deployed web service in Render
2. Click "Environment" tab
3. Add the following environment variables from your PostgreSQL database:

   - **DATABASE_URL**: Copy from your database's "Connection String" (Internal Database URL)
   - **DB_USER**: Copy from your database's username
   - **DB_PASSWORD**: Copy from your database's password

4. Click "Save Changes"
5. The service will automatically redeploy with the new environment variables

### 5. Access Your Service
After deployment, Render will provide:
- Service URL: `https://user-management-service.onrender.com`
- API endpoint: `https://user-management-service.onrender.com/api/v1`

## Environment Variables
The following environment variables need to be configured manually in Render:
- `DATABASE_URL` - PostgreSQL connection string (from database instance)
- `DB_USER` - Database username (from database instance)
- `DB_PASSWORD` - Database password (from database instance)
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
