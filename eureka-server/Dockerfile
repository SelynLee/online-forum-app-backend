# Build the application using Maven
FROM maven:3.8.8-openjdk-17-slim AS build

# Set the working directory for the build
WORKDIR /app

# Copy the Maven project files into the container
COPY pom.xml .
COPY src ./src

# Run the Maven build command to create the JAR file
RUN mvn clean package -DskipTests

# Use OpenJDK to run the application
FROM openjdk:17-jdk-slim

# Set the working directory for the runtime
WORKDIR /app

# Copy the JAR file from the build stage into the runtime stage
COPY --from=build /app/target/eureka-server.jar .

# Expose the Eureka Server port
EXPOSE 8761

# Define the command to run the application
CMD ["java", "-jar", "eureka-server.jar"]
