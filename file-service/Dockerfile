FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/file-service.jar .
EXPOSE 8086
CMD ["java", "-jar", "file-service.jar"]
