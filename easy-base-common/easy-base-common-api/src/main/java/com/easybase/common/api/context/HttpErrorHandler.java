/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.context;

import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling HTTP error responses in a consistent manner.
 * Provides standardized error response handling for token-related exceptions
 * and other common HTTP errors.
 *
 * @author Akhash R
 */
@Slf4j
public final class HttpErrorHandler {

	/**
	 * Handles bad request errors by setting appropriate HTTP status
	 * and response content.
	 *
	 * @param response the HTTP response to configure
	 * @param message custom message for the bad request response
	 * @throws IOException if response writing fails
	 */
	public static void handleBadRequest(
			HttpServletResponse response, String message)
		throws IOException {

		log.debug("Bad request: {}", message);

		writeErrorResponse(
			response, HttpServletResponse.SC_BAD_REQUEST,
			message != null ? message : "bad_request");
	}

	/**
	 * Handles expired token exceptions by setting appropriate HTTP status
	 * and response content.
	 *
	 * @param response the HTTP response to configure
	 * @param exception the expired token exception
	 * @throws IOException if response writing fails
	 */
	public static void handleExpiredToken(
			HttpServletResponse response, ExpiredTokenException exception)
		throws IOException {

		log.debug("Token expired: {}", exception.getMessage());

		writeErrorResponse(
			response, HttpServletResponse.SC_UNAUTHORIZED, "token_expired");
	}

	/**
	 * Handles forbidden access by setting appropriate HTTP status
	 * and response content.
	 *
	 * @param response the HTTP response to configure
	 * @param message custom message for the forbidden response
	 * @throws IOException if response writing fails
	 */
	public static void handleForbidden(
			HttpServletResponse response, String message)
		throws IOException {

		log.debug("Forbidden access: {}", message);

		writeErrorResponse(
			response, HttpServletResponse.SC_FORBIDDEN,
			message != null ? message : "forbidden");
	}

	/**
	 * Handles unexpected internal server errors by setting appropriate
	 * HTTP status and response content.
	 *
	 * @param response the HTTP response to configure
	 * @param exception the unexpected exception
	 * @throws IOException if response writing fails
	 */
	public static void handleInternalServerError(
			HttpServletResponse response, Exception exception)
		throws IOException {

		log.error("Unexpected error in request processing", exception);

		writeErrorResponse(
			response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			"internal_server_error");
	}

	/**
	 * Handles invalid token exceptions by setting appropriate HTTP status
	 * and response content.
	 *
	 * @param response the HTTP response to configure
	 * @param exception the invalid token exception
	 * @throws IOException if response writing fails
	 */
	public static void handleInvalidToken(
			HttpServletResponse response, InvalidTokenException exception)
		throws IOException {

		log.debug("Invalid token: {}", exception.getMessage());

		writeErrorResponse(
			response, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token");
	}

	/**
	 * Writes a standardized error response with the given status code and message.
	 *
	 * @param response the HTTP response to write to
	 * @param statusCode the HTTP status code to set
	 * @param errorMessage the error message to write in response body
	 * @throws IOException if response writing fails
	 */
	public static void writeErrorResponse(
			HttpServletResponse response, int statusCode, String errorMessage)
		throws IOException {

		response.setStatus(statusCode);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		writer.write(errorMessage);
		writer.flush();
	}

	/**
	 * Private constructor to prevent instantiation of utility class.
	 */
	private HttpErrorHandler() {
		throw new UnsupportedOperationException(
			"HttpErrorHandler is a utility class and cannot be instantiated");
	}

}