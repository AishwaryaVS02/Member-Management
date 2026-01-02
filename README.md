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

### Build Success
<img width="1901" height="998" alt="Screenshot 2026-01-02 085259" src="https://github.com/user-attachments/assets/08c31e09-eb16-49d0-956c-9ea6782423ee" />

### JaCoCo Test Coverage Report
<img width="1918" height="943" alt="Screenshot (33)" src="https://github.com/user-attachments/assets/84ed3aea-eb5d-4b46-b3d9-253d1bb6443f" />


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
