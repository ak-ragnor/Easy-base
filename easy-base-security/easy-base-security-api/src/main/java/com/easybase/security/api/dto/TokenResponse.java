/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class TokenResponse {

	public static TokenResponse of(
		String accessToken, String refreshToken, long expiresInSeconds,
		String sessionId) {

		TokenResponse response = new TokenResponse();

		response.setAccessToken(accessToken);
		response.setRefreshToken(refreshToken);
		response.setExpiresIn(expiresInSeconds);
		response.setSessionId(sessionId);
		response.setTokenType("Bearer");

		return response;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	private String accessToken;
	private long expiresIn;
	private String refreshToken;
	private String sessionId;
	private String tokenType;

}