/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApiException {

	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}

	public ResourceNotFoundException(
		String resourceName, String field, Object value) {

		super(
			String.format(
				"%s not found with %s: %s", resourceName, field, value),
			HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}

}