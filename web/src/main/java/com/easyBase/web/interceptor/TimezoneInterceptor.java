package com.easyBase.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.easyBase.common.service.TimezoneService;

/**
 * Interceptor to handle timezone context for requests
 */
@Component
public class TimezoneInterceptor implements HandlerInterceptor {

    @Autowired
    private TimezoneService timezoneService;

    @Value("${app.timezone.header:X-User-Timezone}")
    private String timezoneHeader;

    private static final ThreadLocal<String> TIMEZONE_CONTEXT = ThreadLocal.withInitial(() -> null);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TIMEZONE_CONTEXT.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract timezone from request header
        String userTimezone = request.getHeader(timezoneHeader);

        // Validate and set timezone context
        if (userTimezone != null && timezoneService.isValidTimezone(userTimezone)) {
            TIMEZONE_CONTEXT.set(userTimezone);
            System.out.println("TimezoneInterceptor: Setting user timezone to " + userTimezone);
        } else {
            TIMEZONE_CONTEXT.set(timezoneService.getSystemTimezone());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Add server timezone to response headers
        response.setHeader("X-Server-Timezone", timezoneService.getSystemTimezone());

        String userTimezone = TIMEZONE_CONTEXT.get();
        if (userTimezone != null) {
            response.setHeader("X-User-Timezone", userTimezone);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // Clean up thread local
        TIMEZONE_CONTEXT.remove();
    }

    /**
     * Get the current request's timezone
     */
    public static String getCurrentTimezone() {
        return TIMEZONE_CONTEXT.get();
    }
}