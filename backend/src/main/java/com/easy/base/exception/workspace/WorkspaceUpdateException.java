package com.easy.base.exception.workspace;

import lombok.Getter;

@Getter
public class WorkspaceUpdateException extends RuntimeException {

    public WorkspaceUpdateException(String message) {
        super(message);
    }

    public WorkspaceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
