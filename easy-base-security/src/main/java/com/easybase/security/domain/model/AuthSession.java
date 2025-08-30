package com.easybase.security.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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
