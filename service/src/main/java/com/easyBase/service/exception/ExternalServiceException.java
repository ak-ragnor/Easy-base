package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * External service exception
 *
 * Thrown when external service calls fail.
 * Results in HTTP 502 Bad Gateway responses.
 *
 * Examples:
 * - Payment gateway failures
 * - Email service failures
 * - Third-party API failures
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ExternalServiceException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}