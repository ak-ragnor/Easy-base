package com.easyBase.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Error controller to handle Spring's error forwarding
 */
@RestController
public class ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        System.err.println("=== ErrorController.handleError() called ===");

        // Get error attributes
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (statusCode == null) {
            statusCode = 500;
        }

        if (message == null || message.isEmpty()) {
            message = "An error occurred";
        }

        System.err.println("Status Code: " + statusCode);
        System.err.println("Error Message: " + message);
        System.err.println("Request URI: " + requestUri);

        if (throwable != null) {
            System.err.println("Exception: " + throwable.getClass().getName());
            System.err.println("Exception Message: " + throwable.getMessage());
            throwable.printStackTrace();
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", requestUri != null ? requestUri : request.getRequestURI());

        if (throwable != null) {
            errorResponse.put("exception", throwable.getClass().getSimpleName());
            errorResponse.put("trace", throwable.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }
}