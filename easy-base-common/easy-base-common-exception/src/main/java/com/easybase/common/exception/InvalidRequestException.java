package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class InvalidRequestException extends BaseApiException {

	public InvalidRequestException(String message) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY,
				"INVALID_REQUEST_ERROR");
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNPROCESSABLE_ENTITY,
				"INVALID_REQUEST_ERROR");
	}

	public InvalidRequestException(String message, String errorCode) {
		super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
	}
}
