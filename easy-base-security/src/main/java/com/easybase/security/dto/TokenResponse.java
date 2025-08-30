package com.easybase.security.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

	private String accessToken;

	private String refreshToken;

	private String tokenType;

	private Long expiresIn;

	private Instant expiresAt;

	private UUID userId;

	private UUID tenantId;

	private String userEmail;

	private String userDisplayName;
}
