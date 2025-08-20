package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class UnauthorizedException extends BaseApiException {

	public UnauthorizedException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}
}
