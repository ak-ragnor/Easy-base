/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.context;

import java.util.UUID;

/**
 * Immutable class containing correlation IDs for request tracing.
 * Used for distributed tracing and request correlation across services.
 *
 * @author Akhash R
 */
public class CorrelationIds {

	/**
	 * Creates correlation IDs with auto-generated request ID.
	 *
	 * @param sessionId the session ID, may be null
	 * @param traceId   the trace ID, may be null
	 * @return new correlation IDs instance
	 */
	public static CorrelationIds create(String sessionId, String traceId) {
		UUID uuid = UUID.randomUUID();

		return new CorrelationIds(String.valueOf(uuid), sessionId, traceId);
	}

	/**
	 * Creates correlation IDs for anonymous/system requests.
	 *
	 * @return new correlation IDs with generated request ID
	 */
	public static CorrelationIds system() {
		UUID uuid = UUID.randomUUID();

		return new CorrelationIds(String.valueOf(uuid), null, null);
	}

	public CorrelationIds(String requestId, String sessionId, String traceId) {
		_requestId = requestId;
		_sessionId = sessionId;
		_traceId = traceId;
	}

	public String getRequestId() {
		return _requestId;
	}

	public String getSessionId() {
		return _sessionId;
	}

	public String getTraceId() {
		return _traceId;
	}

	private final String _requestId;
	private final String _sessionId;
	private final String _traceId;

}