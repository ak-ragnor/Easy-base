package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class BusinessException extends BaseApiException {

	public BusinessException(String message) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_ERROR");
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNPROCESSABLE_ENTITY,
				"BUSINESS_ERROR");
	}

	public BusinessException(String message, String errorCode) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
	}
}
