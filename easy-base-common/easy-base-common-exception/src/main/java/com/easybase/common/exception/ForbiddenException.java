package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class ForbiddenException extends BaseApiException {

	public ForbiddenException(String message) {
		super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause, HttpStatus.FORBIDDEN, "FORBIDDEN");
	}
}
