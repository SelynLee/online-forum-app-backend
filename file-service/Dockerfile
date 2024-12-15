FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/file-service.jar .
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
EXPOSE 8086
CMD ["java", "-jar", "file-service.jar"]
