
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/auth-service.jar .
EXPOSE 8085
CMD ["java", "-jar", "auth-service.jar"]