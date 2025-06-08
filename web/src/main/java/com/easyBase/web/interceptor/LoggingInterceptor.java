package com.easyBase.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // Set up MDC for structured logging
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("clientIp", getClientIpAddress(request));

        // Add request ID to response
        response.setHeader("X-Request-ID", requestId);

        logger.info(">>> REQUEST START: {} {} - ID: {}",
                request.getMethod(), request.getRequestURI(), requestId);

        // Log request details
        logRequestDetails(request);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);

        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            // Add performance metrics to MDC
            MDC.put("duration", String.valueOf(duration));
            MDC.put("status", String.valueOf(response.getStatus()));

            logger.info("<<< REQUEST END: {} {} - Duration: {}ms - Status: {}",
                    request.getMethod(), request.getRequestURI(), duration, response.getStatus());

            // Log performance metrics
            performanceLogger.info("PERFORMANCE: {} {} - {}ms",
                    request.getMethod(), request.getRequestURI(), duration);

            // Warn on slow requests
            if (duration > 5000) { // 5 seconds
                logger.warn("SLOW REQUEST: {} {} - Duration: {}ms",
                        request.getMethod(), request.getRequestURI(), duration);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
            logger.error("REQUEST EXCEPTION: {} {} - ID: {} - Exception: {}",
                    request.getMethod(), request.getRequestURI(), requestId, ex.getMessage(), ex);
        }

        // Clean up MDC
        MDC.clear();
    }

    private void logRequestDetails(HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Request details:");
            logger.debug("  Remote Address: {}", request.getRemoteAddr());
            logger.debug("  User Agent: {}", request.getHeader("User-Agent"));
            logger.debug("  Content Type: {}", request.getContentType());
            logger.debug("  Content Length: {}", request.getContentLength());
            logger.debug("  Query String: {}", request.getQueryString());

            // Log important headers
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null) {
                logger.debug("  Accept: {}", acceptHeader);
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                logger.debug("  Authorization: {}", authHeader.length() > 20 ?
                        authHeader.substring(0, 20) + "..." : authHeader);
            }
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}