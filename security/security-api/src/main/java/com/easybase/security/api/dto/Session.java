/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import java.time.Instant;

import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class Session {

	public String getClientIp() {
		return clientIp;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getLastAccessedAt() {
		return lastAccessedAt;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public Instant getRevokedAt() {
		return revokedAt;
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

	public boolean isRevoked() {
		return revoked;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setLastAccessedAt(Instant lastAccessedAt) {
		this.lastAccessedAt = lastAccessedAt;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public void setRevokedAt(Instant revokedAt) {
		this.revokedAt = revokedAt;
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

	private String clientIp;
	private Instant createdAt;
	private String deviceInfo;
	private Instant expiresAt;
	private Instant lastAccessedAt;
	private Map<String, Object> metadata;
	private boolean revoked;
	private Instant revokedAt;
	private String sessionId;
	private UUID tenantId;
	private String userAgent;
	private UUID userId;

}