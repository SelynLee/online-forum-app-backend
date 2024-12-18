
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/email-service.jar .
EXPOSE 8087
CMD ["java", "-jar", "email-service.jar"]