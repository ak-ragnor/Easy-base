/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseApiException {

	public ForbiddenException(String message) {
		super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause, HttpStatus.FORBIDDEN, "FORBIDDEN");
	}

}