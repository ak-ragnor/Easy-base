package com.easyBase.web.controller.base;

import com.easyBase.common.service.TimezoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

/**
 * Base Controller for Enterprise REST APIs
 *
 * Provides common functionality for all controllers:
 * - Request/Response logging
 * - Common headers and metadata
 * - Error handling support
 * - Timezone management
 * - Security context access
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TimezoneService timezoneService;

    /**
     * Add common response headers to all endpoints
     */
    @ModelAttribute
    public void addCommonHeaders(HttpServletRequest request, HttpServletResponse response) {
        // Add common headers
        response.setHeader("X-API-Version", "v1");
        response.setHeader("X-Server-Timezone", timezoneService.getSystemTimezone());
        response.setHeader("X-Request-ID", generateRequestId());
        response.setHeader("X-Timestamp", ZonedDateTime.now().toString());

        // Security headers (if not handled by interceptor)
        if (response.getHeader("X-Content-Type-Options") == null) {
            response.setHeader("X-Content-Type-Options", "nosniff");
        }

        // Performance monitoring
        request.setAttribute("requestStartTime", System.currentTimeMillis());
    }

    /**
     * Generate unique request ID for tracing
     */
    protected String generateRequestId() {
        return "req-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    /**
     * Get current request timezone from header or default
     */
    protected String getCurrentTimezone(HttpServletRequest request) {
        String timezone = request.getHeader("X-User-Timezone");
        return timezone != null ? timezone : timezoneService.getSystemTimezone();
    }

    /**
     * Log request completion with performance metrics
     */
    protected void logRequestCompletion(HttpServletRequest request, String operation) {
        Long startTime = (Long) request.getAttribute("requestStartTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Completed {} in {}ms", operation, duration);
        }
    }
}