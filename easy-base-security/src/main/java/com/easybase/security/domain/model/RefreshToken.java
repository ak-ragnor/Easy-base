package com.easybase.security.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RefreshToken {
	private UUID id;
	private UUID userId;
	private UUID tenantId;
	private UUID sessionId;
	private Instant issuedAt;
	private Instant expiresAt;
	private boolean revoked;
	private String rotationParentId;
}
