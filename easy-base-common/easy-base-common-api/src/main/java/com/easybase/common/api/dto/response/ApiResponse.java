/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Generic API response wrapper.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	public static <T> ApiResponse<T> failure(String message, Object errors) {
		return failure(message, errors, 400);
	}

	public static <T> ApiResponse<T> failure(
		String message, Object errors, Integer statusCode) {

		return new ApiResponse<>(
			false, message, null, errors, statusCode, null);
	}

	public static <T> ApiResponse<T> failure(
		String message, Object errors, Integer statusCode, String path) {

		return new ApiResponse<>(
			false, message, null, errors, statusCode, path);
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, null, data, null, 200, null);
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, message, data, null, 200, null);
	}

	ApiResponse(
		boolean success, String message, T data, Object errors,
		Integer statusCode, String path) {

		this.success = success;
		this.message = message;
		this.data = data;
		this.errors = errors;
		timestamp = LocalDateTime.now();
		this.statusCode = statusCode;
		this.path = path;
	}

	private final T data;
	private final Object errors;
	private final String message;
	private final String path;
	private final Integer statusCode;
	private final boolean success;
	private final LocalDateTime timestamp;

}