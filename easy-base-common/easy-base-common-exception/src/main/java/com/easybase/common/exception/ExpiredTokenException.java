/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

/**
 * Exception thrown when a token has expired and is no longer valid.
 *
 * @author Akhash R
 */
public class ExpiredTokenException extends RuntimeException {

	public ExpiredTokenException(String message) {
		super(message);
	}

	public ExpiredTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}