package com.easy.base.exception.handler;

import com.easy.base.exception.choiceList.ChoiceListNotFoundException;
import com.easy.base.exception.choiceList.DuplicateChoiceListException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ChoiceListExceptionHandler {

    @ExceptionHandler(ChoiceListNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChoiceListNotFound(ChoiceListNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Choice List Not Found",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateChoiceListException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateChoiceList(DuplicateChoiceListException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Duplicate Choice List",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }
}
