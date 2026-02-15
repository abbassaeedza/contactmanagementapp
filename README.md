# Contact Management App

A full-stack contact management web application built with Spring Boot
(backend) and React with TypeScript (frontend).

This project was developed as part of a Virtual Internship Program at
[10Pearls](https://10pearls.com/) (10Pearls Shine) and demonstrates enterprise-grade backend
architecture, secure authentication, testing practices, and clean
frontend integration.

---

## Project Overview

Repository Name: contactmanagementapp\
Type: Multi-user contact management system\
Authentication: JWT-based authentication (no role-based authorization)

The system allows registered users to:

- Register and authenticate securely
- Manage personal contacts (CRUD operations)
- Search contacts
- Access protected routes
- Interact with a paginated API
- Work with audit-tracked entities

---

## Architecture

Layered Architecture:

Controller → Service → Repository → Database

Backend Package Structure:

com.abbasza.contactapi\
├── config\
├── controller\
├── dto\
├── error\
├── model\
│ └── type\
├── repository\
├── security\
└── service

---

## Tech Stack

### Backend

- Java 17
- Spring Boot 3.5.9
- Spring Security (JWT)
- Spring Data JPA
- Hibernate
- BCrypt
- DTO Pattern
- MapStruct
- Lombok
- Global Exception Handling
- Validation (@Valid)
- Global CORS Configuration
- Audit fields (createdAt)
- SLF4J + Logback
- Maven
- JUnit 5 + Mockito
- SonarQube

### Frontend

- React with TypeScript
- Vite
- Tailwind CSS
- React Router

### DevOps & Tooling

- Docker (PostgreSQL & SonarQube)
- Git (feature-based commits)
- SonarQube Quality Analysis

---

## Authentication & Security

- JWT-based stateless authentication
- BCrypt password hashing
- Spring Security filter chain
- Protected API endpoints
- Token validation per request

---

## Core Features

User Features: - Registration - Login / Logout - JWT Authentication -
Protected Routes

Contact Features: - Create Contact - Update Contact - Delete Contact -
Search Contact - Pagination - Audit field tracking

Testing: - Controller Layer Tests - Service Layer Tests - Repository
Layer Tests - Mockito-based mocking - JUnit 5

---

## Getting Started

Prerequisites:

- Java 17
- Nodejs
- Maven
- Docker

### Backend

```
cd backend/contactapi
mvn clean install

Backend runs at: http://localhost:8080
```

---

### Frontend

```
cd frontend/contactui
npm install
npm run dev

Frontend runs at: http://localhost:3000
```

---

## Docker

```
Start containers:

docker-compose up -d

SonarQube: http://localhost:9001
```

---

## Future Improvements

- Role-based authorization
- Swagger/OpenAPI documentation
- Backend Dockerization
- CI/CD integration
- Cloud deployment

---

## Author

[Abbas Zaidi](https://www.linkedin.com/in/abbassaeedza/)\
Developed as part of Virtual Internship Program at [10Pearls](https://10pearls.com/) Shine.
