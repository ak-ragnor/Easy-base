package com.easybase.core.search.exception;

/**
 * Exception thrown by search operations.
 */
public class SearchException extends RuntimeException {

    /**
     * Creates a new SearchException.
     *
     * @param message The error message
     */
    public SearchException(String message) {
        super(message);
    }

    /**
     * Creates a new SearchException with a cause.
     *
     * @param message The error message
     * @param cause The cause of the exception
     */
    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}