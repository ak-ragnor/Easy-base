/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a token has expired and is no longer valid.
 *
 * @author Akhash R
 */
public class ExpiredTokenException extends BaseApiException {

	public ExpiredTokenException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN");
	}

	public ExpiredTokenException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN");
	}

}