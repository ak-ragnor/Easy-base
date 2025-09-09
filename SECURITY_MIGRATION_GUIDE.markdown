# EasyBase Security Module Migration Guide

This guide walks you through migrating from the legacy monolithic security module to the new hybrid JWT + Session security architecture.

## Overview

The new security module provides:
- ✅ **Hybrid JWT + Session authentication**
- ✅ **Modular Maven architecture** (7 modules)
- ✅ **Immediate session revocation capability**
- ✅ **ServiceContext integration** (backward compatible)
- ✅ **Enhanced security features** (device tracking, session limits)
- ✅ **Production-ready** (database migrations, cleanup tasks)

## Migration Strategy

### Phase 1: Preparation (1-2 weeks)

#### 1.1 Review Current Implementation
```bash
# Analyze current security usage
find . -name "*.java" -exec grep -l "AuthenticationFacade\|JwtTokenValidator\|SecurityContext" {} \;

# Check current configuration
grep -r "security\|jwt\|session" application.yml application.properties
```

#### 1.2 Backup Database
```bash
# Create backup before migration
pg_dump easybase_db > easybase_backup_$(date +%Y%m%d_%H%M%S).sql
```

#### 1.3 Set Up Test Environment
```bash
# Clone production database to test environment
# Test new module in isolation
```

### Phase 2: Module Installation (1 week)

#### 2.1 Update Dependencies

**Remove old dependency:**
```xml
<!-- Remove this -->
<dependency>
    <groupId>com.easybase</groupId>
    <artifactId>easy-base-security</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Add new dependency:**
```xml
<!-- Add this -->
<dependency>
    <groupId>com.easybase</groupId>
    <artifactId>easy-base-security-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### 2.2 Update Configuration

**Old Configuration (remove):**
```yaml
app:
  security:
    jwt:
      secret: "old-secret-key"
      expiration: 3600
    session:
      timeout: 1800
```

**New Configuration (add):**
```yaml
easy:
  base:
    security:
      enabled: true
      jwt:
        algorithm: RS256
        access-token-ttl: PT15M
        refresh-token-ttl: P7D
        private-key: |
          -----BEGIN PRIVATE KEY-----
          ...your RSA private key...
          -----END PRIVATE KEY-----
        public-key: |
          -----BEGIN PUBLIC KEY-----
          ...your RSA public key...
          -----END PUBLIC KEY-----
      session:
        default-ttl: P30D
        max-sessions-per-user: 5
        cleanup-interval: PT1H
```

#### 2.3 Database Migration

The new module uses Flyway migrations. Update your configuration:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**New tables will be created:**
- `auth_sessions` - Session storage
- `refresh_tokens` - Refresh token hashes

### Phase 3: Code Migration (2-3 weeks)

#### 3.1 Update Import Statements

**Find and replace across codebase:**
```bash
# Old imports to find
com.easybase.security.adapter.in.auth.AuthenticationFacade
com.easybase.security.adapter.in.auth.JwtTokenValidator
com.easybase.security.domain.model.AuthResult

# New imports to use
com.easybase.security.api.service.AuthenticationFacade
com.easybase.security.api.service.TokenService
com.easybase.security.api.dto.AuthenticatedPrincipalData
```

#### 3.2 Update Authentication Logic

**Before (Legacy):**
```java
@Autowired
private AuthenticationFacade authFacade;

@PostMapping("/login")
public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
    AuthResult result = authFacade.authenticate(request.getUsername(), request.getPassword());
    return ResponseEntity.ok(new TokenResponse(result.getToken()));
}
```

**After (New Module):**
```java
@Autowired
private AuthenticationFacade authFacade;

@Autowired
private TokenService tokenService;

@PostMapping("/login")
public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
    // LoginRequest now includes tenantId, deviceInfo, etc.
    AuthenticatedPrincipalData principal = authFacade.authenticateCredentials(request);

    String accessToken = tokenService.generateAccessToken(principal, Duration.ofMinutes(15));
    String refreshToken = tokenService.generateRefreshToken(principal.getSessionId());

    return ResponseEntity.ok(TokenResponse.of(accessToken, refreshToken, 900, principal.getSessionId()));
}
```

#### 3.3 Update Security Configuration

**Before (Legacy):**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

**After (New Module):**
```java
// Security configuration is now auto-configured!
// Just add custom rules if needed:

@Configuration
public class CustomSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}
```

#### 3.4 Update Controllers

**Before (Legacy):**
```java
@GetMapping("/profile")
public ResponseEntity<UserProfile> getProfile(HttpServletRequest request) {
    String token = extractToken(request);
    AuthResult auth = tokenValidator.validate(token);
    UUID userId = auth.getUserId();
    // ...
}
```

**After (New Module):**
```java
@GetMapping("/profile")
public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
    AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

    UUID userId = principal.getUserId();
    UUID tenantId = principal.getTenantId();
    String sessionId = principal.getSessionId();
    // ...
}
```

#### 3.5 ServiceContext Integration

ServiceContext continues to work as before, but now includes session information:

```java
@Service
public class UserService {

    @Autowired
    private ServiceContextBinding contextBinding;

    public UserProfile getCurrentUser() {
        AuthenticatedPrincipalData principal = contextBinding.fromCurrentContext();

        if (principal != null) {
            UUID userId = principal.getUserId();
            UUID tenantId = principal.getTenantId();
            String sessionId = principal.getSessionId(); // New!
            // ...
        }
        // ...
    }
}
```

### Phase 4: Testing (1-2 weeks)

#### 4.1 Unit Tests

Update existing security unit tests:

```java
@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    @Mock
    private AuthenticationFacade authFacade;

    @Mock
    private TokenService tokenService;

    @Test
    void shouldAuthenticateUser() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setTenantId(UUID.randomUUID());

        AuthenticatedPrincipalData principal = new AuthenticatedPrincipalData();
        principal.setUserId(UUID.randomUUID());
        principal.setTenantId(request.getTenantId());
        principal.setSessionId("session-123");
        principal.setAuthorities(List.of("ROLE_USER"));

        when(authFacade.authenticateCredentials(request)).thenReturn(principal);
        when(tokenService.generateAccessToken(any(), any())).thenReturn("access-token");

        // When & Then
        // ... test logic
    }
}
```

#### 4.2 Integration Tests

Use the provided test base class:

```java
@SpringBootTest(classes = EasyBaseSecurityAutoConfiguration.class)
@ActiveProfiles("test")
public class MySecurityIntegrationTest extends SecurityIntegrationTest {

    @Test
    void shouldLoginAndAccessProtectedResource() {
        // Use methods from base class
        TokenResponse tokens = performLogin("testuser", "password", tenantId);

        mockMvc.perform(get("/api/protected")
                .header("Authorization", "Bearer " + tokens.getAccessToken()))
            .andExpect(status().isOk());
    }
}
```

### Phase 5: Deployment (1 week)

#### 5.1 Blue-Green Deployment Strategy

1. **Deploy new version** to green environment

1. **Run database migrations** automatically via Flyway

1. **Smoke test** authentication endpoints

1. **Switch traffic** gradually using load balancer

1. **Monitor** for errors and performance issues

1. **Rollback** if issues detected (keep blue environment ready)

#### 5.2 Feature Flag Approach (Alternative)

```java
@Component
public class AuthenticationController {

    @Value("${feature.new-auth-enabled:false}")
    private boolean newAuthEnabled;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        if (newAuthEnabled) {
            return newAuthService.login(request);
        } else {
            return legacyAuthService.login(request);
        }
    }
}
```

#### 5.3 Monitoring & Alerting

Add monitoring for key metrics:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true

# Custom metrics to monitor:
# - Authentication success/failure rates
# - Session creation/revocation rates
# - JWT validation latency
# - Database query performance
```

### Phase 6: Cleanup (1 week)

#### 6.1 Remove Legacy Code

After successful deployment and monitoring:

```bash
# Remove old security module dependency from all pom.xml files
# Remove old configuration properties
# Remove unused import statements
# Remove old authentication filters and configurations
```

#### 6.2 Update Documentation

- Update API documentation with new endpoints
- Update deployment guides with new configuration
- Update troubleshooting guides
- Update security policies and procedures

## Rollback Plan

If issues are encountered during migration:

### Immediate Rollback (< 30 minutes)

1. **Switch load balancer** back to old version

1. **Restore database** from backup if schema changes were made

1. **Monitor** application health

1. **Investigate** issues in non-production environment

### Database Rollback

If new tables were created but need to rollback:

```sql
-- Only if absolutely necessary!
DROP TABLE refresh_tokens CASCADE;
DROP TABLE auth_sessions CASCADE;

-- Restore from backup
psql easybase_db < easybase_backup_YYYYMMDD_HHMMSS.sql
```

## Common Migration Issues

### Issue 1: Configuration Property Mismatch

**Problem:** Application fails to start with "Property not found" errors.

**Solution:**
```bash
# Find all old property references
grep -r "app\.security" src/
grep -r "security\.jwt" src/

# Replace with new property structure
# app.security.jwt.secret → easy.base.security.jwt.secret-key
```

### Issue 2: Authentication Principal Type Mismatch

**Problem:** `ClassCastException` when accessing principal.

**Solution:**
```java
// Before
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();

// After
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
if (auth instanceof JwtAuthenticationToken jwtAuth) {
    AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();
    // ...
}
```

### Issue 3: Database Connection Issues

**Problem:** Flyway migrations fail or database connections timeout.

**Solution:**
```yaml
# Increase connection timeout
spring:
  datasource:
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10

  flyway:
    connect-retries: 3
    connect-retries-interval: 10
```

### Issue 4: JWT Key Configuration Issues

**Problem:** JWT tokens fail validation due to key mismatch.

**Solution:**
```bash
# Generate proper RSA key pair
openssl genpkey -algorithm RSA -out private_key.pem -pkcs8 -aes256
openssl pkey -in private_key.pem -pubout -out public_key.pem

# Convert to Spring Boot YAML format
echo "private-key: |" >> application.yml
sed 's/^/  /' private_key.pem >> application.yml
echo "public-key: |" >> application.yml
sed 's/^/  /' public_key.pem >> application.yml
```

## Success Metrics

Track these metrics to measure migration success:

- **Authentication success rate**: > 99.5%
- **API response time**: < 200ms for auth endpoints
- **Database query performance**: < 50ms for session lookups
- **Error rate**: < 0.1% for security-related operations
- **Session cleanup effectiveness**: > 95% expired sessions removed

## Post-Migration Checklist

- [ ] All authentication endpoints working correctly
- [ ] ServiceContext integration functioning
- [ ] Database migrations completed successfully
- [ ] Monitoring and alerting configured
- [ ] Performance metrics within acceptable ranges
- [ ] Security scans completed (no new vulnerabilities)
- [ ] Load testing passed
- [ ] Documentation updated
- [ ] Team training completed
- [ ] Legacy code removed

## Support

For migration assistance:
- **Internal Wiki**: [Security Migration Runbook]
- **Slack Channel**: #security-migration
- **Email**: security-team@easybase.com
- **Emergency Contact**: +1-XXX-XXX-XXXX

---

**Migration completed successfully? 🎉**

Welcome to the new EasyBase Security Module! Your application now benefits from enhanced security, better performance, and improved maintainability.