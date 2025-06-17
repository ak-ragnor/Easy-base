package com.easyBase.security.context;

/**
 * ThreadLocal holder for ServiceContext
 * Manages per-request context storage
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
        ServiceContext context = contextHolder.get();
        if (context == null) {
            context = createEmptyContext();
            contextHolder.set(context);
        }
        return context;
    }

    /**
     * Clear context for current thread
     */
    public static void clearContext() {
        contextHolder.remove();
    }

    /**
     * Create empty context
     */
    private static ServiceContext createEmptyContext() {
        return new ServiceContextImpl();
    }
}
