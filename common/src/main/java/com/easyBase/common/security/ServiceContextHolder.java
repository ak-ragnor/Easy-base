package com.easyBase.common.security;

/**
 * ThreadLocal holder for ServiceContext
 * Manages per-request context storage
 *
 * This class is in the common module to avoid circular dependencies
 */
public class ServiceContextHolder {

    private static final ThreadLocal<ServiceContext> contextHolder = new ThreadLocal<>();

    /**
     * Set context for current thread
     */
    public static void setContext(ServiceContext context) {
        contextHolder.set(context);
    }

    /**
     * Get context for current thread
     */
    public static ServiceContext getContext() {
        return contextHolder.get();
    }

    /**
     * Clear context for current thread
     */
    public static void clearContext() {
        contextHolder.remove();
    }

    /**
     * Check if context exists
     */
    public static boolean hasContext() {
        return contextHolder.get() != null;
    }
}