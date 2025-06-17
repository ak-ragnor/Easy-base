package com.easyBase.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Specialized interceptor for site authentication endpoints
 * Provides detailed logging for authentication attempts
 */
public class SiteAuthLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SiteAuthLoggingInterceptor.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestURI = request.getRequestURI();
        String clientIP = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Extract site information from URL
        String siteInfo = extractSiteFromPath(requestURI);

        // Log authentication attempt
        auditLogger.info("Site authentication attempt - Site: {}, IP: {}, UserAgent: {}, URI: {}",
                siteInfo, clientIP, userAgent, requestURI);

        logger.debug("Processing site authentication request for site: {}", siteInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        String clientIP = getClientIpAddress(request);
        String siteInfo = extractSiteFromPath(requestURI);
        int statusCode = response.getStatus();

        if (ex != null) {
            auditLogger.warn("Site authentication failed - Site: {}, IP: {}, Error: {}, Status: {}",
                    siteInfo, clientIP, ex.getMessage(), statusCode);
        } else if (statusCode >= 200 && statusCode < 300) {
            auditLogger.info("Site authentication successful - Site: {}, IP: {}, Status: {}",
                    siteInfo, clientIP, statusCode);
        } else {
            auditLogger.warn("Site authentication failed - Site: {}, IP: {}, Status: {}",
                    siteInfo, clientIP, statusCode);
        }
    }

    /**
     * Extract site information from request path
     */
    private String extractSiteFromPath(String requestURI) {
        try {
            if (requestURI.contains("/site/")) {
                // Pattern: /api/site/{siteCode}/login
                String[] parts = requestURI.split("/");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("site".equals(parts[i]) && i + 1 < parts.length) {
                        return "code:" + parts[i + 1];
                    }
                }
            } else {
                // Pattern: /api/{siteId}/login
                String[] parts = requestURI.split("/");
                if (parts.length >= 3 && parts[2].matches("\\d+")) {
                    return "id:" + parts[2];
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract site info from path: {}", requestURI);
        }
        return "unknown";
    }

    /**
     * Get client IP address, handling proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}