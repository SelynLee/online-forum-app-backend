
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY target/users-service.jar .

# Expose the service port
EXPOSE 8081

# Define the entry point
CMD ["java", "-jar", "users-service.jar"]