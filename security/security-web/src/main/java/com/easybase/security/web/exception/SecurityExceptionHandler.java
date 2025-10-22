/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.exception;

import com.easybase.security.api.exception.AuthenticationException;
import com.easybase.security.api.exception.InvalidTokenException;
import com.easybase.security.api.exception.RevokedSessionException;
import com.easybase.security.api.exception.SessionExpiredException;
import com.easybase.security.api.exception.TenantMismatchException;

import java.time.Instant;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Akhash
 */
@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, Object>> handleAuthenticationException(
		AuthenticationException authenticationException) {

		log.debug(
			"Authentication failed: {}", authenticationException.getMessage());

		return _createErrorResponse(
			HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
			authenticationException.getMessage());
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidTokenException(
		InvalidTokenException invalidTokenException) {

		log.debug("Invalid token: {}", invalidTokenException.getMessage());

		return _createErrorResponse(
			HttpStatus.UNAUTHORIZED, "INVALID_TOKEN",
			invalidTokenException.getMessage());
	}

	@ExceptionHandler(RevokedSessionException.class)
	public ResponseEntity<Map<String, Object>> handleRevokedSessionException(
		RevokedSessionException revokedSessionException) {

		log.debug("Session revoked: {}", revokedSessionException.getMessage());

		return _createErrorResponse(
			HttpStatus.UNAUTHORIZED, "SESSION_REVOKED",
			revokedSessionException.getMessage());
	}

	@ExceptionHandler(SessionExpiredException.class)
	public ResponseEntity<Map<String, Object>> handleSessionExpiredException(
		SessionExpiredException sessionExpiredException) {

		log.debug("Session expired: {}", sessionExpiredException.getMessage());

		return _createErrorResponse(
			HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED",
			sessionExpiredException.getMessage());
	}

	@ExceptionHandler(TenantMismatchException.class)
	public ResponseEntity<Map<String, Object>> handleTenantMismatchException(
		TenantMismatchException tenantMismatchException) {

		log.warn("Tenant mismatch: {}", tenantMismatchException.getMessage());

		return _createErrorResponse(
			HttpStatus.FORBIDDEN, "TENANT_MISMATCH",
			tenantMismatchException.getMessage());
	}

	@ExceptionHandler(
		{MethodArgumentNotValidException.class, BindException.class}
	)
	public ResponseEntity<Map<String, Object>> handleValidationException(
		Exception exception) {

		Map<String, String> fieldErrors = new HashMap<>();

		if (exception instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException methodArgumentNotValidException =
				(MethodArgumentNotValidException)exception;

			BindingResult bindingResult =
				methodArgumentNotValidException.getBindingResult();

			for (Object error : bindingResult.getAllErrors()) {
				if (error instanceof FieldError) {
					FieldError fieldError = (FieldError)error;

					fieldErrors.put(
						fieldError.getField(), fieldError.getDefaultMessage());
				}
			}
		}
		else if (exception instanceof BindException) {
			BindException bindException = (BindException)exception;

			BindingResult bindingResult = bindException.getBindingResult();

			for (Object error : bindingResult.getAllErrors()) {
				if (error instanceof FieldError) {
					FieldError fieldError = (FieldError)error;

					fieldErrors.put(
						fieldError.getField(), fieldError.getDefaultMessage());
				}
			}
		}

		Map<String, Object> errorResponse = new HashMap<>();

		errorResponse.put("code", "VALIDATION_ERROR");
		errorResponse.put("error", "Validation Failed");
		errorResponse.put("fieldErrors", fieldErrors);
		errorResponse.put("message", "Request validation failed");
		errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
		errorResponse.put("timestamp", String.valueOf(Instant.now()));

		ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();

		return bodyBuilder.body(errorResponse);
	}

	private ResponseEntity<Map<String, Object>> _createErrorResponse(
		HttpStatus status, String code, String message) {

		Map<String, Object> errorResponse = new HashMap<>();

		errorResponse.put("code", code);
		errorResponse.put("error", status.getReasonPhrase());
		errorResponse.put("message", message);
		errorResponse.put("status", status.value());
		errorResponse.put("timestamp", String.valueOf(Instant.now()));

		ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(status);

		return bodyBuilder.body(errorResponse);
	}

}