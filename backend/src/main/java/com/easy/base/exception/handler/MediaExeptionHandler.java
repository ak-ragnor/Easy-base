package com.easy.base.exception.handler;


import com.easy.base.exception.media.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MediaExeptionHandler {

    @ExceptionHandler(ChildFileException.class)
    public ResponseEntity<ErrorResponse>handleChildFileException(ChildFileException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ChildFileException.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateFileNameExeption.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFileNameException(DuplicateFileNameExeption ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                DuplicateFileNameExeption.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ExceededFileNumberException.class)
    public ResponseEntity<ErrorResponse>handleExceededFileNumberException(ExceededFileNumberException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ExceededFileNumberException.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ExceedingFileSizeException.class)
    public ResponseEntity<ErrorResponse> handleExceedingFileSizeException(ExceedingFileSizeException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ChildFileException.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidFileIdException.class)
    public ResponseEntity<ErrorResponse>handleInvalideFileIdException(InvalidFileIdException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                InvalidFileIdException.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UnsupportedFileExtentionException.class)
    public ResponseEntity<ErrorResponse>handleUnsupportedFileExtensionException(UnsupportedFileExtentionException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                UnsupportedFileExtentionException.class.getName(),
                ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }

}
