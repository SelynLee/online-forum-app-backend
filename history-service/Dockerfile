
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/history-service.jar .

# Expose the History Service port
EXPOSE 8083

# Define the command to run the application
CMD ["java", "-jar", "history-service.jar"]