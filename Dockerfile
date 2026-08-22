# Use Eclipse Temurin (OpenJDK) 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml from UserService subdirectory
COPY UserManagement/UserService/mvnw .
COPY UserManagement/UserService/.mvn .mvn
COPY UserManagement/UserService/pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code from UserService subdirectory
COPY UserManagement/UserService/src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port (Render uses port 8080 by default)
EXPOSE 8080

# Run the application with prod profile
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "target/UserService-0.0.1-SNAPSHOT.jar"]
