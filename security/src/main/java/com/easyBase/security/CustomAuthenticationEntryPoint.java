package com.easyBase.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * Custom authentication entry point for handling authentication failures
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jsonResponse = String.format(
                "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"%s\",\"timestamp\":\"%s\",\"path\":\"%s\"}",
                authException != null ? authException.getMessage() : "Unauthorized access",
                ZonedDateTime.now(),
                request.getRequestURI()
        );

        response.getWriter().write(jsonResponse);
    }
}