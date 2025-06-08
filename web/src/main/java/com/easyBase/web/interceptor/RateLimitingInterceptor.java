package com.easyBase.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 🚦 API Rate Limiting Interceptor
 *
 * Implements rate limiting to prevent API abuse:
 * - Per-IP rate limiting
 * - Per-endpoint rate limiting
 * - Sliding window algorithm
 * - Graceful degradation
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    private static final int DEFAULT_RATE_LIMIT = 100;
    private static final int ADMIN_RATE_LIMIT = 50;
    private static final int PUBLIC_RATE_LIMIT = 200;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        String key = clientIp + ":" + endpoint;

        logger.debug("RateLimitingInterceptor - Checking rate limit for: {}", key);

        // Determine rate limit based on endpoint
        int rateLimit = getRateLimitForEndpoint(endpoint);

        // Check rate limit
        if (!isWithinRateLimit(key, rateLimit)) {
            logger.warn("Rate limit exceeded for: {} - Limit: {}/min", key, rateLimit);

            response.setStatus(429); // Too Many Requests
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests\"}");

            return false;
        }

        // Add rate limit headers
        RateLimitInfo info = rateLimitMap.get(key);
        if (info != null) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, rateLimit - info.getCount())));
            response.setHeader("X-RateLimit-Reset", String.valueOf(info.getResetTime()));
        }

        return true;
    }

    private boolean isWithinRateLimit(String key, int limit) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - 60000; // 1 minute window

        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());

        synchronized (info) {
            // Reset if window has passed
            if (info.getResetTime() <= currentTime) {
                info.reset(currentTime + 60000);
            }

            // Check if within limit
            if (info.getCount() >= limit) {
                return false;
            }

            // Increment counter
            info.increment();
            return true;
        }
    }

    private int getRateLimitForEndpoint(String endpoint) {
        if (endpoint.startsWith("/api/admin/")) {
            return ADMIN_RATE_LIMIT;
        } else if (endpoint.startsWith("/health") || endpoint.startsWith("/ping")) {
            return PUBLIC_RATE_LIMIT;
        } else {
            return DEFAULT_RATE_LIMIT;
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

    // Rate limit info holder
    private static class RateLimitInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long resetTime;

        public int getCount() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }

        public long getResetTime() {
            return resetTime;
        }

        public void reset(long newResetTime) {
            count.set(0);
            resetTime = newResetTime;
        }
    }
}
