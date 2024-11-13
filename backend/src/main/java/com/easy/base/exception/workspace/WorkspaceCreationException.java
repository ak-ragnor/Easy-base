package com.easy.base.exception.workspace;

import lombok.Getter;

@Getter
public class WorkspaceCreationException extends RuntimeException {

    public WorkspaceCreationException(String message) {
        super(message);
    }
    public WorkspaceCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}

