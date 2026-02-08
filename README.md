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
<img width="1920" height="1003" alt="Screenshot (41)" src="https://github.com/user-attachments/assets/6377214e-987d-4583-9384-9371ae5d5130" />


### Swagger UI
<img width="1920" height="939" alt="Screenshot (34)" src="https://github.com/user-attachments/assets/22e505f7-d1b4-44ab-a731-8181f76f80ae" />









<img width="1920" height="935" alt="Screenshot (35)" src="https://github.com/user-attachments/assets/dc0ad299-7089-4682-b588-7fe213b7e31f" />









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
