package com.easybase.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class BaseApiException extends RuntimeException {

	private final HttpStatus status;
	private final String errorCode;

	public BaseApiException(String message, HttpStatus status,
			String errorCode) {
		super(message);
		this.status = status;
		this.errorCode = errorCode;
	}

	public BaseApiException(String message, Throwable cause, HttpStatus status,
			String errorCode) {
		super(message, cause);
		this.status = status;
		this.errorCode = errorCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
