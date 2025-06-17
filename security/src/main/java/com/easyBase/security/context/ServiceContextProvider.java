package com.easyBase.security.context;

import org.springframework.stereotype.Component;

@Component
public class ServiceContextProvider {

    /**
     * Get current service context
     */
    public ServiceContext getCurrentContext() {
        return ServiceContextHolder.getContext();
    }

    /**
     * Clear current context
     */
    public void clearContext() {
        ServiceContextHolder.clearContext();
    }

    /**
     * Set context (for testing purposes)
     */
    public void setContext(ServiceContext context) {
        ServiceContextHolder.setContext(context);
    }
}
