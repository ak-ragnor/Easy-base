package com.easyBase.security.jwt;

import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Generic JWT Token Provider
 * Handles token generation and validation without site-specific logic
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.security.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration:86400}")
    private long jwtExpiration; // in seconds

    @Value("${app.jwt.issuer:EasyBase}")
    private String issuer;

    @Value("${app.jwt.audience:EasyBase-API}")
    private String audience;

    /**
     * Get signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(User user) {
        return generateToken(user, new HashMap<>());
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(User user, Map<String, Object> extraClaims) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiry = now.plusSeconds(jwtExpiration);

        Map<String, Object> claims = new HashMap<>();

        // User information
        claims.put("userId", user.getId());
        claims.put("userEmail", user.getEmail());
        claims.put("userName", user.getName());
        claims.put("userRole", user.getRole().name());

        // Session information
        claims.put("sessionId", UUID.randomUUID().toString());
        claims.put("issuedAt", now.toEpochSecond());
        claims.put("expiresAt", expiry.toEpochSecond());

        // Add extra claims if provided
        claims.putAll(extraClaims);

        // Build authorities list
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_" + user.getRole().name());
        claims.put("authorities", authorities);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(expiry.toInstant()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Validate token and extract claims
     */
    public JwtTokenClaims validateTokenAndGetClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return extractTokenClaims(claims);
        } catch (Exception e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user email from token
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Extract claims from JWT
     */
    private JwtTokenClaims extractTokenClaims(Claims claims) {
        JwtTokenClaims tokenClaims = new JwtTokenClaims();

        tokenClaims.setUserId(claims.get("userId", Long.class));
        tokenClaims.setUserEmail(claims.get("userEmail", String.class));
        tokenClaims.setUserName(claims.get("userName", String.class));

        String userRoleStr = claims.get("userRole", String.class);
        if (userRoleStr != null) {
            tokenClaims.setUserRole(UserRole.valueOf(userRoleStr));
        }

        tokenClaims.setSessionId(claims.get("sessionId", String.class));

        // Handle timestamps
        Long issuedAtEpoch = claims.get("issuedAt", Long.class);
        if (issuedAtEpoch != null) {
            tokenClaims.setIssuedAt(ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(issuedAtEpoch), ZoneId.systemDefault()));
        }

        Long expiresAtEpoch = claims.get("expiresAt", Long.class);
        if (expiresAtEpoch != null) {
            tokenClaims.setExpiresAt(ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(expiresAtEpoch), ZoneId.systemDefault()));
        }

        // Extract authorities
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);
        if (authorities != null) {
            tokenClaims.setAuthorities(authorities);
        }

        return tokenClaims;
    }

    // Getters for configuration
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }
}