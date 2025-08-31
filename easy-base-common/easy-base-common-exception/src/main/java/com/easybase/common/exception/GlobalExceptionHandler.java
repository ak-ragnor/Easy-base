/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Akhash R
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseApiException.class)
	public ProblemDetail handleBaseApiException(
		BaseApiException baseApiException) {

		log.error("API error: {}", baseApiException.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			baseApiException.getStatus(), baseApiException.getMessage());

		problemDetail.setTitle(baseApiException.getErrorCode());

		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpectedException(Exception exception) {
		log.error("Unexpected error occurred", exception);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

		problemDetail.setTitle("INTERNAL_ERROR");
		problemDetail.setProperty("debugMessage", exception.getMessage());

		return problemDetail;
	}

}