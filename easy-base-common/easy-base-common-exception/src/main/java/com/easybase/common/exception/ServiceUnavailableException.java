/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends BaseApiException {

	public ServiceUnavailableException(String message) {
		super(message, HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE");
	}

	public ServiceUnavailableException(String message, Throwable cause) {
		super(
			message, cause, HttpStatus.SERVICE_UNAVAILABLE,
			"SERVICE_UNAVAILABLE");
	}

}