# EasyBase Security Module - Hybrid JWT + Session Authentication

A production-ready, modular security module that combines the benefits of JWT tokens with server-side session management for the EasyBase Platform.

## Features

### 🚀 **Hybrid Authentication**
- **Short-lived JWT tokens** (5-15 minutes) for stateless verification
- **Server-side sessions** with immediate revocation capability
- **Dual validation**: Both JWT signature and session state checked on every request

### 🔐 **Enterprise Security**
- **RS256/HS256 JWT signing** with configurable key management
- **Session revocation** - logout from single session or all sessions
- **Refresh token rotation** with replay attack detection
- **Multi-tenant isolation** - tenantId embedded in tokens and sessions
- **Device tracking** with IP, User-Agent, and device metadata

### 🏗️ **Modular Architecture**
- **7 Maven modules** with clear separation of concerns
- **Spring Boot starter** for easy integration
- **ServiceContext integration** - seamless compatibility with existing code
- **JPA-only persistence** - optimized database queries with proper indexing

### 📊 **Production Ready**
- **Database migrations** with Flyway
- **Scheduled cleanup** of expired sessions and tokens
- **Comprehensive logging** and error handling
- **Performance optimized** with strategic database indexes
- **Unit & integration tests** included

---

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.easybase</groupId>
    <artifactId>easy-base-security-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Configure Application

```yaml
easy:
  base:
    security:
      jwt:
        algorithm: RS256
        access-token-ttl: PT15M # 15 minutes
        refresh-token-ttl: P7D # 7 days
        # Use proper RSA keys in production
        private-key: |
          -----BEGIN PRIVATE KEY-----
          ...your private key...
          -----END PRIVATE KEY-----
        public-key: |
          -----BEGIN PUBLIC KEY-----
          ...your public key...
          -----END PUBLIC KEY-----
      session:
        default-ttl: P30D # 30 days
        max-sessions-per-user: 5
```

### 3. Database Setup

The module includes Flyway migrations that automatically create required tables:
- `auth_sessions` - User authentication sessions
- `refresh_tokens` - Refresh token storage (hashed)

### 4. Usage Example

```java
@RestController
public class MyController {

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(Authentication auth) {
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
        AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

        // Access user info
        UUID userId = principal.getUserId();
        UUID tenantId = principal.getTenantId();
        String sessionId = principal.getSessionId();

        return ResponseEntity.ok(getUserProfile(userId));
    }
}
```

---

## API Endpoints

### Authentication Endpoints

#### `POST /api/auth/login`
Authenticate user with credentials and create session.

**Request:**
```json
{
  "username": "user@example.com",
  "password": "password123",
  "tenantId": "550e8400-e29b-41d4-a716-446655440000",
  "deviceInfo": "Chrome on Windows"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiJ9...",
  "expiresIn": 900,
  "sessionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tokenType": "Bearer"
}
```

#### `POST /api/auth/refresh`
Refresh access token using refresh token.

#### `POST /api/auth/logout`
Logout and revoke current session.

#### `GET /api/auth/sessions`
List all active sessions for current user.

#### `DELETE /api/auth/sessions/{sessionId}`
Revoke specific session.

#### `POST /api/auth/sessions/revoke-all`
Revoke all sessions for current user.

### Public Endpoints

#### `GET /.well-known/jwks.json`
JSON Web Key Set for token verification (RS256 only).

---

## Architecture Overview

### Module Structure

```
easy-base-security/
├── easy-base-security-api/ # Pure interfaces & DTOs
├── easy-base-security-core/ # Business logic implementations
├── easy-base-security-jwt/ # JWT token management & JWKS
├── easy-base-security-session/ # Session persistence (JPA)
├── easy-base-security-web/ # REST controllers & filters
├── easy-base-security-starter/ # Spring Boot auto-configuration
└── easy-base-security-test/ # Integration tests & fixtures
```

### Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant F as Filter
    participant T as TokenService
    participant S as SessionService
    participant DB as Database

    C->>+F: Request with Bearer token
    F->>+T: Validate JWT signature & claims
    T-->>-F: JWT valid, extract claims
    F->>+S: Check session active (sessionId)
    S->>+DB: Query session by ID
    DB-->>-S: Return session data
    S-->>-F: Session active & not revoked
    F->>F: Populate SecurityContext & ServiceContext
    F->>C: Continue request processing
```

### Database Schema

**auth_sessions table:**
```sql
- id (BIGSERIAL PRIMARY KEY)
- session_id (VARCHAR UNIQUE) -- UUID string
- user_id (UUID)
- tenant_id (UUID)
- created_at (TIMESTAMP)
- last_accessed_at (TIMESTAMP)
- expires_at (TIMESTAMP)
- revoked (BOOLEAN)
- revoked_at (TIMESTAMP)
- client_ip (VARCHAR)
- user_agent (VARCHAR)
- device_info (VARCHAR)
- metadata (TEXT) -- JSON
```

**refresh_tokens table:**
```sql
- id (BIGSERIAL PRIMARY KEY)
- token_hash (VARCHAR UNIQUE) -- SHA-256 hash
- session_id (VARCHAR FK)
- created_at (TIMESTAMP)
- expires_at (TIMESTAMP)
- revoked (BOOLEAN)
- revoked_at (TIMESTAMP)
```

---

## Security Considerations

### ✅ **Security Features**

1. **Short-lived access tokens** (15 min default) minimize exposure window

1. **Server-side session validation** enables immediate revocation

1. **Refresh token rotation** prevents replay attacks

1. **Token hashing** - refresh tokens stored as SHA-256 hashes

1. **Multi-tenant isolation** - tenantId validated on every request

1. **IP & device tracking** for session monitoring

1. **Configurable session limits** prevent session exhaustion attacks

### 🔒 **Production Checklist**

- [ ] Use strong RSA-2048+ keys for JWT signing
- [ ] Store private keys securely (HashiCorp Vault, AWS KMS, etc.)
- [ ] Enable HTTPS only (`server.ssl.*` properties)
- [ ] Set secure session cookie flags if using cookies
- [ ] Configure rate limiting on auth endpoints
- [ ] Monitor failed authentication attempts
- [ ] Set up log aggregation for security events
- [ ] Regular session cleanup (automated via `SessionCleanupTask`)

---

## Configuration Reference

### JWT Configuration

```yaml
easy:
  base:
    security:
      jwt:
        algorithm: RS256|HS256 # Signing algorithm
        key-id: default # Key identifier for rotation
        issuer: easy-base-auth # JWT issuer claim
        audience: easy-base-platform # JWT audience claim
        access-token-ttl: PT15M # Access token TTL (ISO-8601)
        refresh-token-ttl: P7D # Refresh token TTL
        rotate-refresh-tokens: true # Enable refresh token rotation

        # For HS256 (development only)
        secret-key: "your-secret-key"

        # For RS256 (production)
        private-key: |
          -----BEGIN PRIVATE KEY-----
          ...
          -----END PRIVATE KEY-----
        public-key: |
          -----BEGIN PUBLIC KEY-----
          ...
          -----END PUBLIC KEY-----
```

### Session Configuration

```yaml
easy:
  base:
    security:
      session:
        default-ttl: P30D # Default session lifetime
        sliding-expiration: true # Update last-accessed on use
        max-sessions-per-user: 5 # Concurrent session limit
        cleanup-interval: PT1H # Cleanup task frequency
        cleanup-grace-period: P7D # Keep revoked sessions for audit
```

### Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/easybase
    username: easybase_user
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate # Let Flyway handle schema

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

---

## Migration Guide

### From Legacy Security Module

1. **Add new dependency** and remove old `easy-base-security`

1. **Update configuration** to new property structure

1. **Database migration** - Flyway will create new tables

1. **Code changes** - `AuthenticationFacade` API is mostly compatible

1. **Test thoroughly** - especially ServiceContext integration

### Breaking Changes

- Configuration properties moved from `app.security.*` to `easy.base.security.*`
- New database tables required (`auth_sessions`, `refresh_tokens`)
- Authentication principal type changed to `AuthenticatedPrincipalData`
- Session management API updated with new methods

### Rollback Plan

Keep both modules temporarily:

1. Deploy new module alongside old (different endpoints)

1. Migrate traffic gradually using feature flags

1. Remove old module once fully migrated

---

## Troubleshooting

### Common Issues

**1. "Session not found" errors**
- Check session cleanup configuration
- Verify database connectivity
- Ensure proper indexes are created

**2. JWT validation failures**
- Verify public/private key configuration
- Check token expiration times
- Ensure issuer/audience claims match

**3. ServiceContext not populated**
- Verify `ServiceContextBinding` is properly configured
- Check filter order in Spring Security chain
- Review authentication filter logs

**4. Database connection issues**
- Verify Flyway migrations completed successfully
- Check database user permissions (CREATE, INSERT, UPDATE, DELETE)
- Ensure database timezone settings are correct

### Debug Logging

```yaml
logging:
  level:
    com.easybase.security: DEBUG
    org.springframework.security: DEBUG
    org.flywaydb: DEBUG
```

---

## Performance Optimization

### Database Indexes

The module includes optimized indexes for common query patterns:

```sql
-- Session lookups by ID (primary use case)
CREATE UNIQUE INDEX idx_session_id ON auth_sessions(session_id);

-- User session queries
CREATE INDEX idx_user_tenant ON auth_sessions(user_id, tenant_id);

-- Cleanup queries
CREATE INDEX idx_expires_at ON auth_sessions(expires_at);

-- Active session queries (partial index)
CREATE INDEX idx_active_sessions ON auth_sessions(user_id, tenant_id, expires_at)
WHERE revoked = FALSE;
```

### Memory Optimization

- Sessions are not cached in memory (JPA entity-only)
- JWT validation is stateless after initial key loading
- Cleanup task runs asynchronously to avoid blocking requests

### Scaling Considerations

- **Database**: Use read replicas for session queries
- **Load Balancing**: Sessions are database-stored, no sticky sessions needed
- **Partitioning**: Consider partitioning by `created_at` for high volume
- **Connection Pooling**: Configure appropriate connection pool size

---

## Support & Contribution

### Issues & Bug Reports
Report issues at: [https://github.com/easybase/easy-base/issues](https://github.com/easybase/easy-base/issues)

### Documentation
Full documentation: [https://docs.easybase.com/security](https://docs.easybase.com/security)

### License
Licensed under LGPL-2.1-or-later

---

**Built with ❤️ by the EasyBase Team**