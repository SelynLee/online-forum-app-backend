FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/posts-service.jar .
EXPOSE 8082
CMD ["java", "-jar", "posts-service.jar"]
