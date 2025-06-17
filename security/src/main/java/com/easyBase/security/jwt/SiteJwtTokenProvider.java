package com.easyBase.security.jwt;

import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FIXED Site JWT Token Provider with all required methods
 */
@Component
public class SiteJwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(SiteJwtTokenProvider.class);

    @Value("${security.site.jwt.secret}")
    private String jwtSecret;

    @Value("${security.site.jwt.expiration}")
    private long jwtExpiration;

    @Value("${security.site.jwt.issuer}")
    private String issuer;

    @Value("${security.site.jwt.audience}")
    private String audience;

    /**
     * Getter for JWT expiration (was missing)
     */
    public long getJwtExpiration() {
        return jwtExpiration;
    }

    /**
     * Getter for JWT secret (for validation)
     */
    public String getJwtSecret() {
        return jwtSecret;
    }

    /**
     * Getter for issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Getter for audience
     */
    public String getAudience() {
        return audience;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for user-site authentication
     */
    public String generateSiteToken(User user, Site site, UserSite userSite) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiry = now.plusSeconds(jwtExpiration / 1000);
        String sessionId = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();

        // User information
        claims.put("userId", user.getId());
        claims.put("userEmail", user.getEmail());
        claims.put("userName", user.getName());
        claims.put("userRole", user.getRole().name());

        // Site information
        claims.put("siteId", site.getId());
        claims.put("siteCode", site.getCode());
        claims.put("siteName", site.getName());

        // Site-specific role
        claims.put("siteRole", userSite.getRole().name());

        // Session information
        claims.put("sessionId", sessionId);
        claims.put("issuedAt", now.toEpochSecond());
        claims.put("expiresAt", expiry.toEpochSecond());

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
     * Validate JWT token and extract claims
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

        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("Token expired", e);
        } catch (JwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Extract claims from JWT
     */
    private JwtTokenClaims extractTokenClaims(Claims claims) {
        JwtTokenClaims tokenClaims = new JwtTokenClaims();

        tokenClaims.setUserId(claims.get("userId", Long.class));
        tokenClaims.setUserEmail(claims.get("userEmail", String.class));
        tokenClaims.setUserName(claims.get("userName", String.class));
        tokenClaims.setUserRole(UserRole.valueOf(claims.get("userRole", String.class)));

        tokenClaims.setSiteId(claims.get("siteId", Long.class));
        tokenClaims.setSiteCode(claims.get("siteCode", String.class));
        tokenClaims.setSiteName(claims.get("siteName", String.class));
        tokenClaims.setSiteRole(UserRole.valueOf(claims.get("siteRole", String.class)));

        tokenClaims.setSessionId(claims.get("sessionId", String.class));

        Long issuedAtSeconds = claims.get("issuedAt", Long.class);
        Long expiresAtSeconds = claims.get("expiresAt", Long.class);

        if (issuedAtSeconds != null) {
            tokenClaims.setIssuedAt(ZonedDateTime.ofInstant(Instant.ofEpochSecond(issuedAtSeconds), ZoneId.systemDefault()));
        }
        if (expiresAtSeconds != null) {
            tokenClaims.setExpiresAt(ZonedDateTime.ofInstant(Instant.ofEpochSecond(expiresAtSeconds), ZoneId.systemDefault()));
        }

        return tokenClaims;
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * ADDED: Extract user ID from token without full validation
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ADDED: Extract site ID from token without full validation
     */
    public Long extractSiteId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("siteId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ADDED: Check if token is valid (not expired and properly signed)
     */
    public boolean isTokenValid(String token) {
        try {
            validateTokenAndGetClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ADDED: Refresh token (generate new token with same claims but updated expiry)
     */
    public String refreshToken(String existingToken) {
        try {
            JwtTokenClaims claims = validateTokenAndGetClaims(existingToken);

            // Create new token with same claims but new expiry
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime expiry = now.plusSeconds(jwtExpiration / 1000);

            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("userId", claims.getUserId());
            newClaims.put("userEmail", claims.getUserEmail());
            newClaims.put("userName", claims.getUserName());
            newClaims.put("userRole", claims.getUserRole().name());
            newClaims.put("siteId", claims.getSiteId());
            newClaims.put("siteCode", claims.getSiteCode());
            newClaims.put("siteName", claims.getSiteName());
            newClaims.put("siteRole", claims.getSiteRole().name());
            newClaims.put("sessionId", claims.getSessionId()); // Keep same session ID
            newClaims.put("issuedAt", now.toEpochSecond());
            newClaims.put("expiresAt", expiry.toEpochSecond());

            return Jwts.builder()
                    .claims(newClaims)
                    .subject(claims.getUserEmail())
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .issuedAt(Date.from(now.toInstant()))
                    .expiration(Date.from(expiry.toInstant()))
                    .signWith(getSigningKey())
                    .compact();

        } catch (Exception e) {
            logger.error("Failed to refresh token: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed", e);
        }
    }
}