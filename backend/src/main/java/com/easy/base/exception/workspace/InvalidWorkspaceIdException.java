package com.easy.base.exception.workspace;

import lombok.Getter;

@Getter
public class InvalidWorkspaceIdException extends RuntimeException {
    public InvalidWorkspaceIdException(String message) {
        super(message);
    }

    public InvalidWorkspaceIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
