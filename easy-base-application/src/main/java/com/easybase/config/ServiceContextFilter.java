/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config;

import com.easybase.common.api.context.ContextProvider;
import com.easybase.common.api.context.HttpErrorHandler;
import com.easybase.common.api.context.ServiceContext;
import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Filter that creates and stores ServiceContext for each HTTP request.
 * Uses ContextProvider to build context from request and stores it as
 * a request attribute for access by downstream components.
 *
 * @author Akhash R
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ServiceContextFilter implements Filter {

	public static final String CONTEXT_ATTRIBUTE =
		ServiceContext.class.getName();

	@Override
	public void doFilter(
			ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException {

		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		try {
			ServiceContext context = _contextProvider.build(httpRequest);

			httpRequest.setAttribute(CONTEXT_ATTRIBUTE, context);

			chain.doFilter(request, response);
		}
		catch (ExpiredTokenException expiredTokenException) {
			HttpErrorHandler.handleExpiredToken(
				httpResponse, expiredTokenException);
		}
		catch (InvalidTokenException invalidTokenException) {
			HttpErrorHandler.handleInvalidToken(
				httpResponse, invalidTokenException);
		}
		catch (Exception exception) {
			HttpErrorHandler.handleInternalServerError(httpResponse, exception);
		}
	}

	private final ContextProvider _contextProvider;

}