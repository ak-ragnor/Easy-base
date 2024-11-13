package com.easy.base.exception.workspace;

import lombok.Getter;

@Getter
public class WorkspaceDeletionException extends RuntimeException {
    public WorkspaceDeletionException(String message) {
        super(message);
    }
    public WorkspaceDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
