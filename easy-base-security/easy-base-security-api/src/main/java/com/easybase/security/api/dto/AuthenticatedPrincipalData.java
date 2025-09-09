/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class AuthenticatedPrincipalData {

	public List<String> getAuthorities() {
		return authorities;
	}

	public String getClientIp() {
		return clientIp;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getIssuedAt() {
		return issuedAt;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public String getSessionId() {
		return sessionId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	private List<String> authorities;
	private String clientIp;
	private Instant expiresAt;
	private Instant issuedAt;
	private Map<String, Object> metadata;
	private String sessionId;
	private UUID tenantId;
	private String userAgent;
	private UUID userId;

}