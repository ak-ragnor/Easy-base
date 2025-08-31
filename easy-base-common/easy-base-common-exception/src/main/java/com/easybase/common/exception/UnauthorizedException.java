/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

/**
 * @author Akhash R
 */
public class UnauthorizedException extends BaseApiException {

	public UnauthorizedException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}

}