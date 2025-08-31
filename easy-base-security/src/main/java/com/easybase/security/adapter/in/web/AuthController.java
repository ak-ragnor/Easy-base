/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.security.adapter.in.web;

import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.security.domain.port.in.AuthUseCase;
import com.easybase.security.dto.LoginRequest;
import com.easybase.security.dto.RefreshTokenRequest;
import com.easybase.security.dto.TokenResponse;

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

		TokenResponse response = _authUseCase.login(
			request.getTenantId(), request.getEmail(), request.getPassword(),
			userAgent, _getClientIpAddress(httpRequest));

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
		@RequestBody @Valid RefreshTokenRequest request,
		HttpServletRequest httpRequest) {

		String userAgent = httpRequest.getHeader("User-Agent");

		TokenResponse response = _authUseCase.refresh(
			request.getRefreshToken(), userAgent,
			_getClientIpAddress(httpRequest));

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

	private final AuthUseCase _authUseCase;

}