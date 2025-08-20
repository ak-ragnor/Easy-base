package com.easybase.common.exception;

import org.springframework.http.HttpStatus;

import com.easybase.common.exception.base.BaseApiException;

public class ServiceUnavailableException extends BaseApiException {

	public ServiceUnavailableException(String message) {
		super(message, HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE");
	}

	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause, HttpStatus.SERVICE_UNAVAILABLE,
				"SERVICE_UNAVAILABLE");
	}
}
