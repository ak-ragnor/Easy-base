package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class ValidationException extends BaseApiException {

	public ValidationException(String message) {
		super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
	}

	public ValidationException(String field, String value, String reason) {
		super(String.format(
				"Validation failed for field '%s' with value '%s': %s", field,
				value, reason), HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
	}
}
