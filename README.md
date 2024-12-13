# Online Forum App

This project is a microservices-based online forum application built using Spring Boot and Docker. It includes features such as user management, post creation, authentication, and file management. The services are containerized using Docker and orchestrated with Docker Compose.

---

## **Project Structure**

The application is composed of the following microservices:

1. **Eureka Server**: Service discovery for the microservices.
2. **API Gateway**: Routes requests to the appropriate microservices.
3. **Users Service**: Manages user-related operations.
4. **Posts Service**: Handles post creation and management.
5. **History Service**: Tracks user activity (e.g., viewed posts).
6. **Messages Service**: Allows users to send messages to admins.
7. **Auth Service**: Manages authentication and JWT token generation.
8. **File Service**: Handles file uploads to AWS S3.
9. **Email Service**: Sends email notifications such as registration confirmations.

---

## **Requirements**

### **1. Prerequisites**

Before running this application, ensure the following are installed:

- **Docker** and **Docker Compose**: Required to run and manage containers.
- **Docker Desktop**: If you're using Windows or macOS, Docker Desktop must be installed and running to manage the Docker environment.
- **Java 17**: Optional, for building and running Spring Boot services locally.

To download and install Docker Desktop, visit the official [Docker website](https://www.docker.com/products/docker-desktop).

---

## **Setup Instructions**

### **1. Clone the Repository**

Clone this repository to your local machine using the following commands:

```bash
git clone https://github.com/Oct-Java-Group-Project/online-forum-app-backend.git
cd online-forum-app-backend
```

### **2. Run All Microservices Using Docker**

To run all microservices, use the following command in the root directory (where the `docker-compose.yml` file is located):

```bash
docker compose up --build
```

Once all containers are up and running, you can access the services using their respective URLs

Each service is accessible on the following ports:

- **Eureka Server**: [http://localhost:8761](http://localhost:8761)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Users Service**: [http://localhost:8081](http://localhost:8081)
- **Posts Service**: [http://localhost:8082](http://localhost:8082)
- **History Service**: [http://localhost:8083](http://localhost:8083)
- **Messages Service**: [http://localhost:8084](http://localhost:8084)
- **Auth Service**: [http://localhost:8085](http://localhost:8085)
- **File Service**: [http://localhost:8086](http://localhost:8086)
- **Email Service**: [http://localhost:8087](http://localhost:8087)
