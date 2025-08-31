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
public class InvalidRequestException extends BaseApiException {

	public InvalidRequestException(String message) {
		super(
			message, HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_REQUEST_ERROR");
	}

	public InvalidRequestException(String message, String errorCode) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(
			message, cause, HttpStatus.UNPROCESSABLE_ENTITY,
			"INVALID_REQUEST_ERROR");
	}

}