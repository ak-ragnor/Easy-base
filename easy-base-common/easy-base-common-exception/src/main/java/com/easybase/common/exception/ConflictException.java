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

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseApiException {

	public ConflictException(String message) {
		super(message, HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}

	public ConflictException(String resource, String field, Object value) {
		super(
			String.format(
				"%s with %s '%s' already exists", resource, field, value),
			HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT, "CONFLICT_ERROR");
	}

}