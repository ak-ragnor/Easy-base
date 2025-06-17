package com.easyBase.web.filter;

import com.easyBase.security.context.ServiceContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to ensure ServiceContext ThreadLocal is properly cleaned up
 * Prevents memory leaks in application server thread pools
 */
public class ServiceContextCleanupFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceContextCleanupFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ServiceContextCleanupFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);

        } finally {
            try {
                ServiceContextHolder.clearContext();
            } catch (Exception e) {
                logger.warn("Error clearing ServiceContext: {}", e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {
        logger.info("ServiceContextCleanupFilter destroyed");
    }
}