# Courier User Service

## Description

The **Courier User Service** is a microservice responsible for managing users and roles within
the application. It handles authentication, authorization, and integrates with the
`resource-service` for managing users of type `client`.

---

## Features

- User creation, update, and deactivation
- Role management with uniqueness validations and permission control.
- Integration with `resource-service` via OpenFeign for `client` management.
- JWT token validation for authentication and authorization.
- `Spring Security` implementation for securing endpoints.

---

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring Security**
- **Spring Data JPA** (with MySQL)
- **Spring Cloud OpenFeign**
- **Lombok**
- **MapStruct** for DTO mapping
- **JWT (JSON Web Token)**

---

## Project Structure

```
user-service/
├── src/main/java/com/courier/userservice/
│   ├── config/                   # Security and FeignClient configuration
│   ├── controller/               # REST controllers
│   ├── exception/                # Global exception handlers
│   ├── feignclient/              # Feign clients for external service communication
│   ├── manager/                  # Business logic management
│   ├── objects/
│   │   ├── criteria/             # Query criteria specifications
│   │   ├── dto/                  # Data Transfer Objects (DTOs)
│   │   ├── entity/               # JPA Entities
│   │   ├── mapper/               # DTO to entity mapping
│   ├── repository/               # JPA repositories
│   ├── service/
│   │   ├── impl/                 # Service implementations
│   │   ├── ErrorLogService.java  # Service for error logging
│   │   ├── JwtService.java       # Service for JWT handling
│   │   ├── PublicKeyService.java # Service for public key handling
│   │   ├── RoleService.java      # Service for role management
│   │   ├── UserService.java      # Main user service logic
│   ├── CourierUserServiceApplication.java  # Main application class
└── ...
```

---

## Authentication & Security

This service uses `JWT` for authentication and authorization.

- The `JwtAuthenticationFilter` extracts the token from cookies and validates authentication.
- Permissions are managed via `@PreAuthorize` in controllers.
- The authenticated user details can be accessed using `authentication.princial`.

---

## Integration with `resource-service`

`UserService` communicates with the `resource-service` for managing `client` using `OpenFeign`.

```java
@FeignClient(name = "resource-service", configuration = FeignClientConfig.class)
public interface ResourceFeignClient {

  @PostMapping("/api/courier/resource/contact")
  ContactDto createContact(@RequestBody ContactDto contactDto);

  @PostMapping("/api/courier/resource/contact/{id}")
  ContactDto updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto);

  @DeleteMapping("/api/courier/resource/contact/{id}")
  void disableContact(@PathVariable Long id);

  @GetMapping("/api/courier/resource/contact/phone/{phoneNumber}")
  ContactDto getContactByPhone(@PathVariable String phoneNumber);
}
```

---

## Key Endpoints

### `UserController`

| Method   | Endpoint                               | Description                                                                                   |
| -------- | -------------------------------------- | --------------------------------------------------------------------------------------------- |
| `GET`    | `/api/courier/user`                    | Get all users (paginated).                                                                    |
| `GET`    | `/api/courier/user/all`                | Get all users (non-paginated).                                                                |
| `GET`    | `/api/courier/user/{userId}`           | Get user by ID (Admins, Couriers can view any user; Clients can only view their own profile). |
| `POST`   | `/api/courier/user`                    | Create a new user (admin/courier/client).                                                     |
| `PUT`    | `/api/courier/user/{userId}`           | Update an existing user.                                                                      |
| `DELETE` | `/api/courier/user/{userId}`           | Disable a user (only `admin`).                                                                |
| `GET`    | `/api/courier/user/search?search=text` | Search users by fullName, phoneNumber, email, roles.                                          |

### `RoleController`

| Method   | Endpoint                     | Description                                                          |
| -------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`    | `/api/courier/role/all`      | Get all roles.                                                       |
| `GET`    | `/api/courier/role/{roleId}` | Get a role by ID.                                                    |
| `POST`   | `/api/courier/role`          | Create a new role.                                                   |
| `PUT`    | `/api/courier/role/{roleId}` | Update an existing role.                                             |
| `DELETE` | `/api/courier/role/{roleId}` | Disable a role (only if it is not the only role assigned to a user). |

---

## Client Creation Flow

1. **A `ClientDto` is sento to `/api/courier/user`**.
2. **The system validates if the user already exists (unique email/phone check).**
3. **If it is a `client`, a `ContactDto` is sento to `resource-service`.**
4. **The user is saved in the database.**
5. **A `ClientDto` with complete details is returned.**

---

## Environment Variables Configuration

```yml
spring:
  application:
    name: courier-user-service

  datasource:
    url: jdbc:mysql://localhost:3306/user_db?useSSL=false&createDatabaseIfNotExist=true&serverTimezone=UTC
    username: your-username
    password: your-password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: user-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*" # Allow all packages to be deserialized
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.StringSerializer

  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 5000
            readTimeout: 5000
            loggerLevel: full
      circuitbreaker:
        enabled: true

server:
  port: 8084

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true

  instance:
    hostname: courier-user-service
```

---

## Running the Project

1. Ensure that `MySQL` and `Kafka` are running.
2. Register `user-service` with `Eureka` if using service discovery.
3. Compile and run the application:

```sh
mvn clean install
mvn spring-boot:run
```

4. The service will be available at `http://localhost:8084`.

---

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
