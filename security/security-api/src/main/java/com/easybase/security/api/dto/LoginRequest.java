/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class LoginRequest {

	public String getClientIp() {
		return clientIp;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public String getPassword() {
		return password;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getUserName() {
		return userName;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String clientIp;
	private String deviceInfo;
	private Map<String, Object> metadata;

	@NotBlank
	private String password;

	private UUID tenantId;
	private String userAgent;

	@NotBlank
	private String userName;

}