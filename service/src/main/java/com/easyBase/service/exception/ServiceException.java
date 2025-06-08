// ===== BASE EXCEPTION =====

package com.easyBase.service.exception;

/**
 * Base exception for all service layer exceptions
 *
 * Provides common functionality for all business exceptions:
 * - Error codes for internationalization
 * - Structured error information
 * - Chaining support
 * - Enterprise logging integration
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public abstract class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final Object[] parameters;

    protected ServiceException(String message) {
        super(message);
        this.errorCode = null;
        this.parameters = null;
    }

    protected ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.parameters = null;
    }

    protected ServiceException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    protected ServiceException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters != null ? parameters.clone() : null;
    }
}