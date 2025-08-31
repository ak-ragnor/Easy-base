/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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