# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/messages-service.jar .

# Expose the Messages Service port
EXPOSE 8084

# Define the command to run the application
CMD ["java", "-jar", "messages-service.jar"]
