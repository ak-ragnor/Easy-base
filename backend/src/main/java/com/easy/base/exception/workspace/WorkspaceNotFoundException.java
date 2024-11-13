package com.easy.base.exception.workspace;

import lombok.Getter;

@Getter
public class WorkspaceNotFoundException extends RuntimeException {
    public WorkspaceNotFoundException(String message) {
        super(message);
    }

    public WorkspaceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
