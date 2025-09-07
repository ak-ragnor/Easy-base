/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.in.web;

import com.easybase.infrastructure.api.dto.response.ApiResponse;
import com.easybase.security.adapter.in.web.dto.LoginRequest;
import com.easybase.security.adapter.in.web.dto.RefreshTokenRequest;
import com.easybase.security.adapter.in.web.dto.TokenResponse;
import com.easybase.security.domain.model.AuthToken;
import com.easybase.security.domain.port.in.AuthUseCase;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Akhash R
 */
@RequestMapping("/easy-base/api/auth")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Map<String, UUID>>> getCurrentUser(
		@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);

		return ResponseEntity.ok(
			ApiResponse.success(
				Map.of(
					"userId", _authUseCase.getCurrentUserId(sessionToken),
					"tenantId",
					_authUseCase.getCurrentTenantId(sessionToken))));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponse>> login(
		@RequestBody @Valid LoginRequest request,
		HttpServletRequest httpRequest) {

		String userAgent = httpRequest.getHeader("User-Agent");

		AuthToken authToken = _authUseCase.login(
			request.getTenantId(), request.getEmail(), request.getPassword(),
			userAgent, _getClientIpAddress(httpRequest));

		TokenResponse response = _toTokenResponse(authToken);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
		@RequestBody @Valid RefreshTokenRequest request,
		HttpServletRequest httpRequest) {

		String userAgent = httpRequest.getHeader("User-Agent");

		AuthToken authToken = _authUseCase.refresh(
			request.getRefreshToken(), userAgent,
			_getClientIpAddress(httpRequest));

		TokenResponse response = _toTokenResponse(authToken);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/revoke")
	public ResponseEntity<ApiResponse<Void>> revoke(
		@RequestHeader("Authorization") String authHeader) {

		_authUseCase.revoke(_extractHeader(authHeader));

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("/revoke-all")
	public ResponseEntity<ApiResponse<Void>> revokeAll(
		@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);

		_authUseCase.revokeAll(
			_authUseCase.getCurrentUserId(sessionToken),
			_authUseCase.getCurrentTenantId(sessionToken));

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("/validate")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> validate(
		@RequestHeader("Authorization") String authHeader) {

		boolean valid = _authUseCase.validateToken(_extractHeader(authHeader));

		return ResponseEntity.ok(ApiResponse.success(Map.of("valid", valid)));
	}

	private String _extractHeader(String authHeader) {
		if ((authHeader == null) || !authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Invalid Authorization header");
		}

		return authHeader.substring(7);
	}

	private String _getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");

		if ((xForwardedFor != null) && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIP = request.getHeader("X-Real-IP");

		if ((xRealIP != null) && !xRealIP.isEmpty()) {
			return xRealIP;
		}

		return request.getRemoteAddr();
	}

	private TokenResponse _toTokenResponse(AuthToken authToken) {
		TokenResponse response = new TokenResponse();

		response.setAccessToken(authToken.getAccessToken());
		response.setRefreshToken(authToken.getRefreshToken());
		response.setTokenType(authToken.getTokenType());
		response.setExpiresIn(authToken.getExpiresIn());
		response.setExpiresAt(authToken.getExpiresAt());
		response.setUserId(authToken.getUserId());
		response.setTenantId(authToken.getTenantId());
		response.setUserEmail(authToken.getUserEmail());
		response.setUserDisplayName(authToken.getUserDisplayName());

		return response;
	}

	private final AuthUseCase _authUseCase;

}