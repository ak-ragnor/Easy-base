package com.easy.base.exception.handler;

import com.easy.base.exception.workspace.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WorkspaceExceptionHandler {
    @ExceptionHandler(WorkspaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkspaceNotFound(WorkspaceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Workspace not found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkspaceCreationException.class)
    public ResponseEntity<ErrorResponse> handleWorkspaceCreation(WorkspaceCreationException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Workspace creation failed",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WorkspaceUpdateException.class)
    public ResponseEntity<ErrorResponse> handleWorkspaceUpdate(WorkspaceUpdateException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Workspace update failed",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WorkspaceDeletionException.class)
    public ResponseEntity<ErrorResponse> handleWorkspaceDeletion(WorkspaceDeletionException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Workspace deletion failed",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidWorkspaceIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWorkspaceId(InvalidWorkspaceIdException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid workspace ID format",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }
}