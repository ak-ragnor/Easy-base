package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

/**
 * Validation exception with detailed field-level errors
 *
 * Thrown when complex validation fails that goes beyond simple JSR-303 validation.
 * Results in HTTP 422 Unprocessable Entity responses.
 *
 * Examples:
 * - Cross-field validation errors
 * - Business rule validation errors
 * - Complex constraint violations
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final Map<String, List<String>> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = null;
    }

    public ValidationException(String message, Map<String, List<String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String errorCode, String message, Map<String, List<String>> fieldErrors) {
        super(errorCode, message);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }
}