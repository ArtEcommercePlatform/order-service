# Use OpenJDK 17 on Alpine Linux as the base image
FROM openjdk:17-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/order-service-0.0.1-SNAPSHOT.jar /app/order-service.jar

# Expose the application's port
EXPOSE 8084

# Add a health check (optional, checks every 30s with a 5s timeout)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]
