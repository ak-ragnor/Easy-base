package com.easybase.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.easybase.common.exception.base.BaseApiException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseApiException.class)
	public ProblemDetail handleBaseApiException(BaseApiException ex) {
		log.error("API error: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail
				.forStatusAndDetail(ex.getStatus(), ex.getMessage());

		problemDetail.setTitle(ex.getErrorCode());

		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpectedException(Exception ex) {
		log.error("Unexpected error occurred", ex);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred");

		problemDetail.setTitle("INTERNAL_ERROR");
		problemDetail.setProperty("debugMessage", ex.getMessage());

		return problemDetail;
	}
}
