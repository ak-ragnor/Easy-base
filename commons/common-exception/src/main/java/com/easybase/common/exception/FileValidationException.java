/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.exception;

import com.easybase.common.exception.base.BaseApiException;
import org.springframework.http.HttpStatus;

/**
 * @author Saura
 * Date:06/11/25
 * Time:12:08 pm
 */
public class FileValidationException extends BaseApiException {
    public FileValidationException(String message) {
        super(message, HttpStatus.EXPECTATION_FAILED, "EXPECTATION_FAILED");
    }
    public FileValidationException(String resource, String field, Object value) {
        super(
                String.format(
                        "%s with %s '%s' already exists", resource, field, value),
                HttpStatus.EXPECTATION_FAILED, "EXPECTATION_FAILED");
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.EXPECTATION_FAILED, "EXPECTATION_FAILED");
    }
}
