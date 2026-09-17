@echo off
echo ========================================
echo Starting E-commerce Microservices
echo ========================================
echo.

echo Starting User Management Service on port 8081...
cd user-management-service
start "User Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting Products Management Service on port 8082...
cd Products-management-service
start "Products Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting Admin Management Service on port 8083...
cd Admin-management-service
start "Admin Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting Order Management Service on port 8084...
cd Order-management-service
start "Order Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting Notification Management Service on port 8085...
cd notification-management-service
start "Notification Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting Cart Management Service on port 8086...
cd Cart-management-service
start "Cart Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 /nobreak

echo Starting API Gateway Service on port 8087...
cd api-gateway-service
start "API Gateway Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo All services are starting...
echo ========================================
echo.
echo Service URLs:
echo - User Service: http://localhost:8081/api/v1
echo - Products Service: http://localhost:8082/api
echo - Admin Service: http://localhost:8083/api/admin
echo - Order Service: http://localhost:8084/api/v1
echo - Notification Service: http://localhost:8085/api/notifications
echo - Cart Service: http://localhost:8086/api/v1
echo - API Gateway: http://localhost:8087
echo.
echo Gateway Routes:
echo - User routes: http://localhost:8087/api/v1/**
echo - Products routes: http://localhost:8087/api/**
echo - Admin routes: http://localhost:8087/api/admin/**
echo - Order routes: http://localhost:8087/api/v1/orders/**
echo - Cart routes: http://localhost:8087/cart/**
echo - Notification routes: http://localhost:8087/api/notifications/**
echo.
echo Swagger Documentation:
echo - User Service: http://localhost:8081/swagger-ui.html
echo - Products Service: http://localhost:8082/swagger-ui.html
echo - Admin Service: http://localhost:8083/api/admin/swagger-ui.html
echo - Order Service: http://localhost:8084/swagger-ui.html
echo - Notification Service: http://localhost:8085/swagger-ui.html
echo - Cart Service: http://localhost:8086/swagger-ui.html
echo.
pause
