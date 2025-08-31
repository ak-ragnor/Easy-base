/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseApiException {

	public BusinessException(String message) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_ERROR");
	}

	public BusinessException(String message, String errorCode) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
	}

	public BusinessException(String message, Throwable cause) {
		super(
			message, cause, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_ERROR");
	}

}