package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class ConflictException extends BaseApiException {

	public ConflictException(String message) {
		super(message, HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}

	public ConflictException(String resource, String field, Object value) {
		super(String.format("%s with %s '%s' already exists", resource, field,
				value), HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}
}
