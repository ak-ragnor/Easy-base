/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.exception;

/**
 * @author Akhash
 */
public abstract class SecurityException extends RuntimeException {

	protected SecurityException(String message) {
		super(message);
	}

	protected SecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}