# EasyBase Server

Backend server module for the EasyBase platform.

## Structure

```
src/
├── main/
│ ├── java/
│ │ └── com/easybase/easybaseserver/
│ │ ├── config/ # Configuration classes
│ │ ├── controllers/ # REST controllers
│ │ ├── dto/ # Data Transfer Objects
│ │ ├── entities/ # JPA entities
│ │ ├── exceptions/ # Custom exceptions
│ │ ├── filters/ # Security filters
│ │ ├── repositories/ # Data repositories
│ │ ├── security/ # Security configuration
│ │ ├── services/ # Business logic
│ │ └── utils/ # Utility classes
│ └── resources/
│ ├── db/migration/ # Flyway migrations
│ └── application.yml # Application configuration
└── test/ # Test files
```

## Running the Server

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## API Documentation

Once running, access Swagger UI at:
http://localhost:8080/api/swagger-ui.html