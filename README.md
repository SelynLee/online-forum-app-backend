# Online Forum App

This project is a microservices-based online forum application built using Spring Boot and Docker. It offers features such as user management, post creation, authentication, and file management. The services are containerized with Docker and orchestrated using Docker Compose.

---

## **Project Structure**

The application consists of the following microservices:

1. **Eureka Server**: Provides service discovery for microservices.
2. **API Gateway**: Routes incoming requests to the appropriate microservices.
3. **Users Service**: Manages user-related operations.
4. **Posts Service**: Handles post creation and management.
5. **History Service**: Tracks user activities (e.g., viewed posts).
6. **Messages Service**: Enables users to send messages to administrators.
7. **Auth Service**: Manages authentication and JWT token generation.
8. **File Service**: Handles file uploads to AWS S3.
9. **Email Service**: Sends email notifications, such as registration confirmations.

---

## **Requirements**

### **1. Prerequisites**

Ensure the following are installed before running the application:

- **Docker** and **Docker Compose**: Required to run and manage containers.
- **Docker Desktop**: Necessary for managing the Docker environment on Windows or macOS.
- **Java 17**: Optional, for building and running Spring Boot services locally.

To download Docker Desktop, visit the official [Docker website](https://www.docker.com/products/docker-desktop).

---

## **Setup Instructions**

### **1. Clone the Repository**

Clone the repository to your local machine using the following commands:

```bash
git clone https://github.com/Oct-Java-Group-Project/online-forum-app-backend.git
cd online-forum-app-backend
```

### **2. Build and Run Microservices Using Docker**

Before running the microservices, perform the following steps:

1. Navigate to the root directory of the project (where the `docker-compose.yml` is located) and execute the following Maven command to clean, compile, and install all services:

   ```bash
   mvn clean install -T 2C
   ```

2. From the root directory (where the `docker-compose.yml` file is located) and run:

   ```bash
   docker compose up --build
   ```

### **3. Access Microservices**

Once all containers are running, you can access the services at their respective URLs:

- **Eureka Server**: [http://localhost:8761](http://localhost:8761)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Users Service**: [http://localhost:8081](http://localhost:8081)
- **Posts Service**: [http://localhost:8082](http://localhost:8082)
- **History Service**: [http://localhost:8083](http://localhost:8083)
- **Messages Service**: [http://localhost:8084](http://localhost:8084)
- **Auth Service**: [http://localhost:8085](http://localhost:8085)
- **File Service**: [http://localhost:8086](http://localhost:8086)
- **Email Service**: [http://localhost:8087](http://localhost:8087)
