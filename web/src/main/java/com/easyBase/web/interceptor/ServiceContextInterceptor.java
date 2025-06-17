package com.easyBase.web.interceptor;

import com.easyBase.security.context.ServiceContext;
import com.easyBase.security.context.ServiceContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor for ServiceContext management and logging
 */
public class ServiceContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        ServiceContext context = ServiceContextHolder.getContext();

        if (context.isAuthenticated()) {
            _log.debug("Request {} {} - User: {}, Site: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    context.getCurrentUser() != null ? context.getCurrentUser().getEmail() : "unknown",
                    context.getCurrentSite() != null ? context.getCurrentSite().getCode() : "unknown");
        } else {
            _log.debug("Request {} {} - No authentication context",
                    request.getMethod(),
                    request.getRequestURI());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        // Optional: Add context information to response headers for debugging
        ServiceContext context = ServiceContextHolder.getContext();

        if (context.isAuthenticated() && _log.isDebugEnabled()) {
            response.addHeader("X-Debug-User", context.getCurrentUser().getEmail());
            response.addHeader("X-Debug-Site", context.getCurrentSite().getCode());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

        if (ex != null) {
            _log.error("Request {} {} completed with error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage());
        } else {
            _log.debug("Request {} {} completed successfully",
                    request.getMethod(),
                    request.getRequestURI());
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(ServiceContextInterceptor.class);
}
