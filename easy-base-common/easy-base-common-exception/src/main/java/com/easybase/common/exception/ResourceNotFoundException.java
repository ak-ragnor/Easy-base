package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class ResourceNotFoundException extends BaseApiException {

	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}

	public ResourceNotFoundException(String resourceName, String field,
			Object value) {
		super(String.format("%s not found with %s: %s", resourceName, field,
				value), HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
	}
}
