package com.easy.base.exception.user;

import lombok.Getter;

@Getter
public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException(String message) {
        super(message);
    }

    public InvalidUserIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
