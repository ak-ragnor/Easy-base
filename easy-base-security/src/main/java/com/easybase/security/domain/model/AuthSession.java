package com.easybase.security.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthSession {
	private UUID id;
	private UUID userId;
	private UUID tenantId;
	private String sessionToken;
	private Instant expiresAt;
	private String userAgent;
	private String ipAddress;
	private boolean revoked;
	private Instant createdAt;
	private Instant updatedAt;
}
