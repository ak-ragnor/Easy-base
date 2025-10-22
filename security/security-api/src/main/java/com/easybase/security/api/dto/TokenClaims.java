/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class TokenClaims {

	public String getAudience() {
		return audience;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getIssuedAt() {
		return issuedAt;
	}

	public String getIssuer() {
		return issuer;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getSessionId() {
		return sessionId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	private String audience;
	private Instant expiresAt;
	private Instant issuedAt;
	private String issuer;
	private List<String> roles;
	private String sessionId;
	private UUID tenantId;
	private String tokenId;
	private UUID userId;

}