package com.easyBase.web.interceptor;

import com.easyBase.common.security.ServiceContext;
import com.easyBase.common.security.ServiceContextHolder;
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
                    context.getCurrentUserId() != null ? context.getCurrentUserId() : "unknown",
                    context.getCurrentSiteId() != null ? context.getCurrentSiteId() : "unknown");
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
