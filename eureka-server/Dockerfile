
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/eureka-server.jar .

# Expose the Eureka Server port
EXPOSE 8761

# Define the command to run the application
CMD ["java", "-jar", "eureka-server.jar"]