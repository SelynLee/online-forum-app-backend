
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/file-service.jar .
EXPOSE 8086

# ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
# ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
CMD ["java", "-jar", "file-service.jar"]