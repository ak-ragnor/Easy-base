/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;

import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class CreateSessionRequest {

	public String getClientIp() {
		return clientIp;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public Duration getTtl() {
		return ttl;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public void setTtl(Duration ttl) {
		this.ttl = ttl;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	private String clientIp;
	private String deviceInfo;
	private Map<String, Object> metadata;

	@NotNull
	private UUID tenantId;

	private Duration ttl;
	private String userAgent;

	@NotNull
	private UUID userId;

}