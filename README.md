# Easy Base Spring Application

A modular Spring-based enterprise application with integrated React frontend and optional Elasticsearch support.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 13+ (for production)

### Build and Run

1. **Setup Project Structure**
   ```bash
   # Already done by running this script
   ```

2. **Build Application**
   ```bash
   ant all
   ```

3. **Run Development Server**
   ```bash
   ant dev-server
   ```

4. **Deploy to Tomcat**
   ```bash
   ant deploy-local
   ```

### Available Build Targets

- `ant all` - Complete build process
- `ant quick` - Quick build without tests
- `ant clean` - Clean all build artifacts
- `ant test` - Run all tests
- `ant package` - Package as WAR
- `ant bundle` - Create Tomcat bundle

### Environment Profiles

- **Development**: HSQLDB + Embedded components
- **Testing**: HSQLDB + TestContainers
- **Production**: PostgreSQL + Elasticsearch cluster

### Configuration

Configuration files are located in:
- `deployment/config/environment/` - Environment-specific properties
- `config/src/main/resources/database/` - Database configuration
- `deployment/config/logging/` - Logging configuration

## Architecture

The application follows a modular architecture with clear separation of concerns:

- **Common**: Shared DTOs, utilities, and constants
- **Config**: Spring configuration and beans
- **Domain**: JPA entities and repositories
- **Service**: Business logic and external integrations
- **Security**: Authentication and authorization
- **Integration**: External system integrations
- **Monitoring**: Health checks and metrics
- **Web**: REST controllers and web configuration
- **Frontend**: React application

## Package Structure

All Java classes use the `com.easyBase` package:
- `com.easyBase.common` - Common utilities
- `com.easyBase.config` - Spring configuration
- `com.easyBase.domain` - Domain entities and repositories
- `com.easyBase.service` - Business services
- `com.easyBase.security` - Security components
- `com.easyBase.web` - Web controllers

## Development

### Running Tests
```bash
ant test-unit          # Unit tests only
ant test-integration   # Integration tests only
```

### Database Setup
```bash
ant setup-database
```

### Frontend Development
```bash
cd frontend
npm start             # Development server
npm run build         # Production build
```

## Deployment

The build process creates a complete Tomcat bundle with:
- Pre-configured Tomcat server
- Application WAR file
- Environment-specific configuration
- Database setup scripts
- Monitoring and management scripts

Extract and run:
```bash
tar -xzf target/dist/easy-base-tomcat-bundle-*.tar.gz
cd tomcat-bundle
./scripts/start.sh
```

## Support

For issues and questions, check the documentation in the `docs/` directory.
