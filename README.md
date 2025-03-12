## Overview

EasyBase is a zero-configuration backend development framework designed for rapid application development. It combines the best of traditional SQL databases with Elasticsearch's powerful search capabilities in a dual storage architecture with automatic synchronization.

**Key Features:**

- **Zero-Configuration Philosophy**: Get started with minimal setup steps
- **Dual Storage Architecture**: SQL database (HSQLDB/PostgreSQL) + Elasticsearch
- **Code Generation**: Create full Spring Boot modules from simple YAML definitions
- **Dynamic Collections**: Create and manage collections (tables) at runtime
- **Rich Search Capabilities**: Powerful search, filter, and query functionalities
- **React-Based Admin Dashboard**: Comprehensive system management interface
- **RESTful API**: Automatically generated endpoints with full CRUD operations
- **Relationship Management**: Sophisticated handling of cross-collection relationships

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Node.js 18+ (for Admin UI development)

### Quick Start

```bash
# Clone the repository
git clone https://github.com/your-organization/easybase.git
cd easybase

# Build the project
mvn clean install

# Run the application
cd dist/target
java -jar easybase.jar
```

### Running in Development Mode

```bash
# Start the server in development mode
cd dist/target
java -jar easybase.jar --dev

# Start the Admin UI in development mode
cd admin/frontend
npm run dev
```

## Project Structure

```
easybase/
├── pom.xml                    # Parent POM
│
├── core/                      # Core functionality
│   ├── pom.xml
│   └── src/main/java/...      # Core components
│
├── common/                    # Shared utilities
│   ├── pom.xml
│   └── src/main/java/...      # Common utilities and models
│
├── admin/                     # Admin UI
│   ├── pom.xml
│   ├── src/                  # Backend
│   └── frontend/             # React application
│
├── starter/                  # Spring Boot starter
│   ├── pom.xml
│   └── src/
│
├── tools/                    # Tools directory
│   ├── pom.xml
│   └── generator/           # Code generation tool
│       ├── pom.xml
│       └── src/
│
├── apps/                     # Generated applications
│   ├── pom.xml
│   └── [generated-modules]/  # Generated application modules
│
└── dist/                    # Distribution
    ├── pom.xml
    └── src/
```

## Getting Started

Once the application is running, you can access:

- **Main Application**: http://localhost:8090
- **Admin Dashboard**: http://localhost:8090/_/
- **API Endpoint**: http://localhost:8090/api/

### First-Time Setup

On first run, EasyBase will:
1. Generate a setup token
2. Open your browser to the setup page
3. Guide you through creating an admin account

### Creating Your First Collection

You can create collections either through:

1. **Admin Dashboard**: Navigate to Collections > Create New
2. **API**: Send a POST request to `/api/collections`
3. **Code Generation**: Define a YAML file and generate code

## Code Generation

EasyBase includes a powerful code generation system that creates full Spring Boot modules from YAML definitions.

### Creating a Module

1. Create a YAML definition file:

```yaml
# sample-user.yml
module: easybase-user
entity: User
table: eb_user
packageName: com.easybase.user

fields:
  - name: id
    type: UUID
    primaryKey: true
    generated: true
    
  - name: email
    type: String
    length: 255
    nullable: false
    validation:
      - type: Email
      - type: NotBlank
    search:
      type: text
      analyzer: email
      fields:
        keyword: true
    
  - name: status
    type: Enum
    enumClass: UserStatus
    values: [ACTIVE, INACTIVE]
    default: ACTIVE
    
finders:
  - name: findByEmail
    parameters:
      - name: email
        type: String
    returnType: Optional<User>
```

2. Generate the module:

```bash
mvn com.easybase:generator:generate -Dservice=user
```

3. Build and run the generated module:

```bash
mvn com.easybase:generator:build -Dservice=user
cd apps/easybase-user
mvn spring-boot:run
```

## Collections API

EasyBase provides a RESTful API for working with collections.

### Basic CRUD Operations

```
# Create a new record
POST /api/collections/{collection}

# Get a record by ID
GET /api/collections/{collection}/{id}

# Update a record
PUT /api/collections/{collection}/{id}

# Delete a record
DELETE /api/collections/{collection}/{id}

# Query records
GET /api/collections/{collection}?filter=field eq value&search=term&sort=field:asc
```

### Querying Capabilities

The API supports:
- **Filtering**: Using OData filter syntax (`field eq value`, `field gt value`, etc.)
- **Searching**: Full-text search across all text fields
- **Sorting**: Multiple field sorting with direction (`field:asc`, `field:desc`)
- **Pagination**: Using standard `page` and `size` parameters

## Configuration

### Core Configuration

```yaml
# application.yml
server:
  port: 8090

storage:
  type: embedded  # or postgresql
  embedded:
    path: ./data/hsqldb
  postgresql:
    url: jdbc:postgresql://localhost:5432/easybase
    username: postgres
    password: password

elasticsearch:
  embedded: true
  host: localhost
  port: 9200

admin:
  path: /_/
```

### Security Configuration

```yaml
security:
  auth:
    type: basic  # or jwt or oauth2
    jwt:
      secret: your-secret-key
      expiration: 86400000  # 24 hours in milliseconds
```

## Development Guide

### Building from Source

```bash
# Full build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Generate distribution
mvn clean install -P dist
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific module tests
cd core
mvn test
```

### Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -am 'Add my feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Create a new Pull Request

## Architecture

EasyBase uses a hybrid architecture combining plain Spring Framework for core modules and Spring Boot for application modules.

### Core Components

- **Collection Management**: Defines and manages collection schemas
- **Storage Layer**: SQL database + Elasticsearch with synchronization
- **Search Engine**: Provides powerful search and filtering capabilities
- **Authentication System**: User management and security
- **Admin Dashboard**: System configuration and monitoring

### Synchronization Engine

The system maintains consistency between SQL and Elasticsearch through:
- Transaction-based synchronization
- Batch processing
- Consistency verification
- Error handling and recovery

## Best Practices

- **Entity Definition**: Use clear naming conventions
- **Relationship Design**: Choose appropriate relationship types
- **Search Configuration**: Configure field mappings for optimal search
- **Security**: Implement proper access controls
- **Performance**: Use appropriate indexing and caching strategies

## Troubleshooting

### Common Issues

1. **Database connection issues**
   - Check database credentials and connection string
   - Verify database server is running

2. **Elasticsearch issues**
   - Check Elasticsearch connection settings
   - Verify enough disk space is available

3. **Application startup failures**
   - Check log files in `logs/` directory
   - Verify Java version (requires Java 17+)

### Getting Help

- Create an issue in the GitHub repository
- Check the documentation for more detailed information
- Join our community Discord server
