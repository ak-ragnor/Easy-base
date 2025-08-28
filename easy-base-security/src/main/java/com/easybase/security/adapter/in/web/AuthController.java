package com.easybase.security.adapter.in.web;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.security.domain.port.in.AuthUseCase;
import com.easybase.security.dto.LoginRequest;
import com.easybase.security.dto.RefreshTokenRequest;
import com.easybase.security.dto.TokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/easy-base/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponse>> login(
			@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {

		String userAgent = httpRequest.getHeader("User-Agent");
		String ipAddress = _getClientIpAddress(httpRequest);

		TokenResponse response = _authUseCase.login(request, userAgent,
				ipAddress);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(
			@Valid @RequestBody RefreshTokenRequest request,
			HttpServletRequest httpRequest) {

		String userAgent = httpRequest.getHeader("User-Agent");
		String ipAddress = _getClientIpAddress(httpRequest);

		TokenResponse response = _authUseCase.refresh(request, userAgent,
				ipAddress);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/revoke")
	public ResponseEntity<ApiResponse<Void>> revoke(
			@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);

		_authUseCase.revoke(sessionToken);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("/revoke-all")
	public ResponseEntity<ApiResponse<Void>> revokeAll(
			@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);

		UUID userId = _authUseCase.getCurrentUserId(sessionToken);
		UUID tenantId = _authUseCase.getCurrentTenantId(sessionToken);

		_authUseCase.revokeAll(userId, tenantId);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("/validate")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> validate(
			@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);
		boolean isValid = _authUseCase.validateToken(sessionToken);

		Map<String, Boolean> response = Map.of("valid", isValid);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Map<String, UUID>>> getCurrentUser(
			@RequestHeader("Authorization") String authHeader) {

		String sessionToken = _extractHeader(authHeader);

		UUID userId = _authUseCase.getCurrentUserId(sessionToken);
		UUID tenantId = _authUseCase.getCurrentTenantId(sessionToken);

		Map<String, UUID> response = Map.of("userId", userId, "tenantId",
				tenantId);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	private String _extractHeader(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Invalid Authorization header");
		}

		return authHeader.substring(7);
	}

	private String _getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");

		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIP = request.getHeader("X-Real-IP");

		if (xRealIP != null && !xRealIP.isEmpty()) {
			return xRealIP;
		}

		return request.getRemoteAddr();
	}

	private final AuthUseCase _authUseCase;
}
