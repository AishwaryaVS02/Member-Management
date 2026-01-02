# MemberService Application

## APIs
- `POST /api/v1/auth/login`
- `GET /api/v1/members`
- `GET /api/v1/members/{id}`
- `POST /api/v1/members`
- `PUT /api/v1/members/{id}`
- `DELETE /api/v1/members/{id}`

## Features
- CRUD operations for MemberService
- JWT authentication
- Caching using in-memory cache
- Unit test cases with JaCoCo report generation
- Logging and handling of necessary exceptions

## Screenshots
<img width="1920" height="1080" alt="Screenshot (33)" src="https://github.com/user-attachments/assets/7c4f2b94-3875-4cef-a36b-b13bc66e8867" />

### JaCoCo Test Coverage Report

## Technologies, Tools, and Build Tools

### Technologies
- Java 17
- Spring Boot 3.5.9 (Web, Data JPA, Security, Validation, Cache)
- PostgreSQL 17.6
- Flyway (Database migration)
- JWT (JSON Web Tokens)

### Tools
- Lombok
- JaCoCo (Code coverage)
- AssertJ (Unit testing assertions)
- JUnit 5 / Spring Boot Test / Spring Security Test
- Postman (for API testing)
- IntelliJ IDEA

### Build Tools / Plugins
- Gradle
- Spring Boot Gradle Plugin 3.5.9
- Spring Dependency Management Plugin 1.1.7
- Java Plugin
- JaCoCo Plugin 0.8.11 
