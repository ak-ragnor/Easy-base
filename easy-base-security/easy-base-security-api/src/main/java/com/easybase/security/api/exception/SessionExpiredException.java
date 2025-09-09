/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.exception;

/**
 * @author Akhash
 */
public class SessionExpiredException extends SecurityException {

	public SessionExpiredException(String message) {
		super(message);
	}

	public SessionExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

}