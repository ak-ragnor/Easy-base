/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a token is invalid due to format errors,
 * signature verification failures, or other validation issues.
 *
 * @author Akhash R
 */
public class InvalidTokenException extends BaseApiException {

	public InvalidTokenException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
	}

	public InvalidTokenException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
	}

}