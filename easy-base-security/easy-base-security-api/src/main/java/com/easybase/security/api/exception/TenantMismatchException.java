/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.exception;

/**
 * @author Akhash
 */
public class TenantMismatchException extends SecurityException {

	public TenantMismatchException(String message) {
		super(message);
	}

	public TenantMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

}