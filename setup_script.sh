#!/bin/bash

# Easy Base Spring Application Setup Script
# This script creates the complete directory structure and basic configuration files

set -e

PROJECT_NAME="easy-base"
BASE_DIR=$(pwd)

echo "========================================="
echo "Easy Base Spring Application Setup"
echo "========================================="
echo "Creating project structure for: $PROJECT_NAME"
echo "Base directory: $BASE_DIR"
echo ""

# Create main project directories
echo "Creating main project structure..."

mkdir -p $PROJECT_NAME/{common,config,domain,service,security,integration,monitoring,web,frontend}
mkdir -p $PROJECT_NAME/deployment/{tomcat,scripts,config}
mkdir -p $PROJECT_NAME/docs
mkdir -p $PROJECT_NAME/logs

# Create common module structure
echo "Setting up common module..."
mkdir -p $PROJECT_NAME/common/src/{main,test}/java/com/easyBase/common/{dto,enums,exceptions,constants,utils,validation,annotations}
mkdir -p $PROJECT_NAME/common/src/main/resources/{messages,schemas}
mkdir -p $PROJECT_NAME/common/src/test/resources

# Create config module structure
echo "Setting up config module..."
mkdir -p $PROJECT_NAME/config/src/{main,test}/java/com/easyBase/config/{database,security,web,elasticsearch,cache,integration}
mkdir -p $PROJECT_NAME/config/src/main/resources/{spring,database,cache,environment}
mkdir -p $PROJECT_NAME/config/src/test/resources

# Create domain module structure
echo "Setting up domain module..."
mkdir -p $PROJECT_NAME/domain/src/{main,test}/java/com/easyBase/domain/{entity,repository,specification,converter}
mkdir -p $PROJECT_NAME/domain/src/main/java/com/easyBase/domain/entity/{base,user,product,order,audit}
mkdir -p $PROJECT_NAME/domain/src/main/java/com/easyBase/domain/repository/{base,jpa,search}
mkdir -p $PROJECT_NAME/domain/src/main/resources/{META-INF,db/migration,elasticsearch}
mkdir -p $PROJECT_NAME/domain/src/main/resources/db/migration/{hsqldb,postgresql}
mkdir -p $PROJECT_NAME/domain/src/test/resources

# Create service module structure
echo "Setting up service module..."
mkdir -p $PROJECT_NAME/service/src/{main,test}/java/com/easyBase/service/{base,business,integration,search,security,mapper,validator}
mkdir -p $PROJECT_NAME/service/src/test/java/{service,integration}
mkdir -p $PROJECT_NAME/service/src/test/resources

# Create security module structure
echo "Setting up security module..."
mkdir -p $PROJECT_NAME/security/src/{main,test}/java/com/easyBase/security/{authentication,authorization,jwt,oauth2,audit,encryption}
mkdir -p $PROJECT_NAME/security/src/test/resources

# Create integration module structure
echo "Setting up integration module..."
mkdir -p $PROJECT_NAME/integration/src/{main,test}/java/com/easyBase/integration/{messaging,email,storage,payment,external,scheduler}
mkdir -p $PROJECT_NAME/integration/src/test/resources

# Create monitoring module structure
echo "Setting up monitoring module..."
mkdir -p $PROJECT_NAME/monitoring/src/{main,test}/java/com/easyBase/monitoring/{metrics,health,logging,tracing}
mkdir -p $PROJECT_NAME/monitoring/src/test/resources

# Create web module structure
echo "Setting up web module..."
mkdir -p $PROJECT_NAME/web/src/{main,test}/java/com/easyBase/web/{controller,filter,interceptor,advice,converter,swagger}
mkdir -p $PROJECT_NAME/web/src/main/java/com/easyBase/web/controller/{base,api/v1,admin,public}
mkdir -p $PROJECT_NAME/web/src/main/resources/{static,templates}
mkdir -p $PROJECT_NAME/web/src/main/resources/templates/{email,reports}
mkdir -p $PROJECT_NAME/web/src/main/webapp/WEB-INF/{spring,jsp}
mkdir -p $PROJECT_NAME/web/src/main/webapp/static
mkdir -p $PROJECT_NAME/web/src/test/resources

# Create frontend module structure
echo "Setting up frontend module..."
mkdir -p $PROJECT_NAME/frontend/src/{components,store,services,utils,hooks,styles,assets}
mkdir -p $PROJECT_NAME/frontend/src/components/{common,forms,layout,pages}
mkdir -p $PROJECT_NAME/frontend/src/components/common/{Button,Modal,Table,Form}
mkdir -p $PROJECT_NAME/frontend/src/components/forms/{UserForm,ProductForm,OrderForm}
mkdir -p $PROJECT_NAME/frontend/src/components/layout/{Header,Sidebar,Footer,Layout}
mkdir -p $PROJECT_NAME/frontend/src/components/pages/{Dashboard,Users,Products,Orders}
mkdir -p $PROJECT_NAME/frontend/src/store/{slices,middleware,selectors}
mkdir -p $PROJECT_NAME/frontend/src/assets/{images,icons,fonts}
mkdir -p $PROJECT_NAME/frontend/public

# Create deployment structure
echo "Setting up deployment structure..."
mkdir -p $PROJECT_NAME/deployment/tomcat
mkdir -p $PROJECT_NAME/deployment/scripts/{database,monitoring}
mkdir -p $PROJECT_NAME/deployment/config/{environment,logging}

# Create initial configuration files
echo "Creating initial configuration files..."

# Build properties
cat > $PROJECT_NAME/build.properties << 'EOF'
# Build Configuration
app.name=easy-base
app.version=1.0.0-SNAPSHOT

# Maven profiles
maven.profile.dev=dev
maven.profile.test=test
maven.profile.prod=prod

# Database profiles
db.profile.dev=hsqldb
db.profile.test=hsqldb
db.profile.prod=postgresql

# Frontend build
frontend.build.env=production
frontend.build.analyze=false

# Tomcat configuration
tomcat.version=9.0.85
tomcat.port=8080
tomcat.memory.initial=512m
tomcat.memory.max=2048m
EOF

# Environment-specific properties
cat > $PROJECT_NAME/deployment/config/environment/development.properties << 'EOF'
# Development Environment Configuration

# Server configuration
server.port=8080
server.servlet.context-path=/easy-base

# Database configuration
db.driver=org.hsqldb.jdbc.JDBCDriver
db.url=jdbc:hsqldb:mem:devdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
db.username=sa
db.password=
db.pool.min=2
db.pool.max=10

# JPA configuration
jpa.hibernate.ddl-auto=create-drop
jpa.hibernate.dialect=org.hibernate.dialect.HSQLDialect
jpa.hibernate.show_sql=true
jpa.hibernate.format_sql=true

# Elasticsearch configuration
elasticsearch.enabled=false
elasticsearch.host=localhost
elasticsearch.port=9200

# Cache configuration
cache.enabled=true
cache.provider=ehcache

# Security configuration
security.jwt.secret=dev-secret-key-change-in-production
security.jwt.expiration=3600000
security.cors.allowed-origins=http://localhost:3000

# Logging configuration
logging.level.com.easyBase=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate=INFO
EOF

cat > $PROJECT_NAME/deployment/config/environment/production.properties << 'EOF'
# Production Environment Configuration

# Server configuration
server.port=8080
server.servlet.context-path=/easy-base

# Database configuration (JNDI)
db.jndi.name=java:comp/env/jdbc/easyBaseDB
db.pool.min=10
db.pool.max=50

# JPA configuration
jpa.hibernate.ddl-auto=validate
jpa.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
jpa.hibernate.show_sql=false

# Elasticsearch configuration
elasticsearch.enabled=true
elasticsearch.host=${ELASTICSEARCH_HOST:localhost}
elasticsearch.port=${ELASTICSEARCH_PORT:9200}
elasticsearch.cluster=${ELASTICSEARCH_CLUSTER:production}

# Cache configuration
cache.enabled=true
cache.provider=redis
cache.redis.host=${REDIS_HOST:localhost}
cache.redis.port=${REDIS_PORT:6379}

# Security configuration
security.jwt.secret=${JWT_SECRET}
security.jwt.expiration=3600000
security.cors.allowed-origins=${CORS_ORIGINS:}

# Logging configuration
logging.level.com.easyBase=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
EOF

# Database properties for different environments
cat > $PROJECT_NAME/config/src/main/resources/database/database-dev.properties << 'EOF'
# Development Database Configuration
db.driver=org.hsqldb.jdbc.JDBCDriver
db.url=jdbc:hsqldb:mem:devdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
db.username=sa
db.password=

# JPA Configuration
jpa.hibernate.ddl-auto=create-drop
jpa.hibernate.dialect=org.hibernate.dialect.HSQLDialect
jpa.hibernate.show_sql=true
jpa.hibernate.format_sql=true
EOF

cat > $PROJECT_NAME/config/src/main/resources/database/database-prod.properties << 'EOF'
# Production Database Configuration
db.jndi.name=java:comp/env/jdbc/easyBaseDB

# JPA Configuration
jpa.hibernate.ddl-auto=validate
jpa.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
jpa.hibernate.show_sql=false
jpa.hibernate.format_sql=false
EOF

# Logging configuration
cat > $PROJECT_NAME/deployment/config/logging/log4j2.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        
        <RollingFile name="FileAppender" fileName="logs/easy-base.log"
                     filePattern="logs/easy-base-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
        
        <RollingFile name="AuditAppender" fileName="logs/audit.log"
                     filePattern="logs/audit-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <DefaultRolloverStrategy max="90"/>
        </RollingFile>
    </Appenders>
    
    <Loggers>
        <Logger name="com.easyBase" level="INFO" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Logger>
        
        <Logger name="com.easyBase.audit" level="INFO" additivity="false">
            <AppenderRef ref="AuditAppender"/>
        </Logger>
        
        <Logger name="org.springframework" level="WARN"/>
        <Logger name="org.hibernate" level="WARN"/>
        
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
EOF

# Create deployment scripts
echo "Creating deployment scripts..."

# Database setup script
cat > $PROJECT_NAME/deployment/scripts/database/setup-dev.sh << 'EOF'
#!/bin/bash
# Development Database Setup Script

echo "Setting up development database (HSQLDB)..."
echo "No additional setup required for HSQLDB in-memory database"
echo "Database will be created automatically on application startup"
EOF

cat > $PROJECT_NAME/deployment/scripts/database/setup-prod.sh << 'EOF'
#!/bin/bash
# Production Database Setup Script

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-easybasedb}
DB_USER=${DB_USER:-easybase}

echo "Setting up production database (PostgreSQL)..."
echo "Host: $DB_HOST:$DB_PORT"
echo "Database: $DB_NAME"
echo "User: $DB_USER"

# Create database and user
psql -h $DB_HOST -p $DB_PORT -U postgres -c "CREATE DATABASE $DB_NAME;"
psql -h $DB_HOST -p $DB_PORT -U postgres -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';"
psql -h $DB_HOST -p $DB_PORT -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"

echo "Database setup completed"
EOF

# Application startup script
cat > $PROJECT_NAME/deployment/scripts/start.sh << 'EOF'
#!/bin/bash
# Application Startup Script

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_DIR="$SCRIPT_DIR/../apache-tomcat-*"
APP_PROFILE=${APP_PROFILE:-dev}

echo "Starting Easy Base Application..."
echo "Profile: $APP_PROFILE"

# Set environment variables
export SPRING_PROFILES_ACTIVE=$APP_PROFILE
export JAVA_OPTS="-Xms512m -Xmx2048m -Dspring.profiles.active=$APP_PROFILE"

# Start Tomcat
if [ -d "$TOMCAT_DIR" ]; then
    $TOMCAT_DIR/bin/startup.sh
    
    echo "Application starting..."
    echo "Check logs at: $TOMCAT_DIR/logs/"
    echo "Application will be available at: http://localhost:8080/easy-base"
else
    echo "Error: Tomcat directory not found at $TOMCAT_DIR"
    exit 1
fi
EOF

# Application shutdown script
cat > $PROJECT_NAME/deployment/scripts/stop.sh << 'EOF'
#!/bin/bash
# Application Shutdown Script

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_DIR="$SCRIPT_DIR/../apache-tomcat-*"

echo "Stopping Easy Base Application..."

if [ -d "$TOMCAT_DIR" ]; then
    $TOMCAT_DIR/bin/shutdown.sh
    echo "Application stopped"
else
    echo "Error: Tomcat directory not found at $TOMCAT_DIR"
    exit 1
fi
EOF

# Make scripts executable
chmod +x $PROJECT_NAME/deployment/scripts/*.sh
chmod +x $PROJECT_NAME/deployment/scripts/database/*.sh

# Create README files
echo "Creating documentation..."

cat > $PROJECT_NAME/README.md << 'EOF'
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
EOF

# Create .gitignore
cat > $PROJECT_NAME/.gitignore << 'EOF'
# Build artifacts
target/
build/
dist/
logs/

# IDE files
.idea/
.vscode/
*.iml
*.ipr
*.iws
.project
.classpath
.settings/

# OS files
.DS_Store
Thumbs.db

# Node modules and build artifacts
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Environment files
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Database files
*.db
*.h2.db

# Temporary files
*.tmp
*.temp
*.log
*.pid

# Security
*.key
*.pem
*.crt

# Backup files
*.bak
*.orig
EOF

# Create initial Git repository
cd $PROJECT_NAME
git init
git add .
git commit -m "Initial Easy Base project setup"

echo ""
echo "========================================="
echo "Project setup completed successfully!"
echo "========================================="
echo ""
echo "Project location: $BASE_DIR/$PROJECT_NAME"
echo ""
echo "Next steps:"
echo "1. cd $PROJECT_NAME"
echo "2. Review and customize configuration files"
echo "3. Run 'ant all' to build the application"
echo "4. Run 'ant dev-server' to start development"
echo ""
echo "Available commands:"
echo "  ant help           - Show all available build targets"
echo "  ant all            - Complete build process"
echo "  ant dev-server     - Start development server"
echo ""
echo "Happy coding! 🚀"