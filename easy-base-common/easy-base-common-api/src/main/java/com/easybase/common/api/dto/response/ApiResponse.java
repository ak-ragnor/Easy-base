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