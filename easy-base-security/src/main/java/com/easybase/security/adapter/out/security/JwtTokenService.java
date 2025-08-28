package com.easybase.security.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService {

	public String generateAccessToken(UUID userId, UUID tenantId,
			String email) {

		return Jwts.builder().subject(userId.toString())
				.claim("tenantId", tenantId.toString()).claim("email", email)
				.claim("type", "access").issuedAt(new Date())
				.expiration(_getExpiry(_jwtExpirationSeconds))
				.signWith(_getSigningKey(), Jwts.SIG.HS256).compact();
	}

	public Claims getClaimsFromToken(String token) {

		return Jwts.parser().verifyWith(_getSigningKey()).build()
				.parseSignedClaims(token).getPayload();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(_getSigningKey()).build()
					.parseSignedClaims(token);

			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("JWT validation failed: {}", e.getMessage());

			return false;
		}
	}

	public UUID getUserIdFromToken(String token) {

		return UUID.fromString(getClaimsFromToken(token).getSubject());
	}

	public UUID getTenantIdFromToken(String token) {

		return UUID.fromString(
				getClaimsFromToken(token).get("tenantId", String.class));
	}

	public String getEmailFromToken(String token) {
		return getClaimsFromToken(token).get("email", String.class);
	}

	public boolean isTokenExpired(String token) {
		try {
			return getClaimsFromToken(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (JwtException e) {
			log.debug("Invalid token while checking expiration: {}",
					e.getMessage());

			return true;
		}
	}

	public boolean isAccessToken(String token) {

		return "access"
				.equals(getClaimsFromToken(token).get("type", String.class));
	}

	private Date _getExpiry(long seconds) {

		return new Date(System.currentTimeMillis() + seconds * 1000);
	}

	private SecretKey _getSigningKey() {

		return Keys.hmacShaKeyFor(_jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	@Value("${easy-base.security.jwt.secret:mysecretmysecretmysecretmysecret}")
	private String _jwtSecret;

	@Value("${easy-base.security.jwt.expiration:3600}")
	private long _jwtExpirationSeconds;
}
