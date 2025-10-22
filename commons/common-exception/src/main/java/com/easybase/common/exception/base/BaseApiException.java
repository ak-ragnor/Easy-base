/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception.base;

import org.springframework.http.HttpStatus;

/**
 * @author Akhash R
 */
public abstract class BaseApiException extends RuntimeException {

	public BaseApiException(
		String message, HttpStatus status, String errorCode) {

		super(message);

		this.status = status;
		this.errorCode = errorCode;
	}

	public BaseApiException(
		String message, Throwable cause, HttpStatus status, String errorCode) {

		super(message, cause);

		this.status = status;
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	private final String errorCode;
	private final HttpStatus status;

}