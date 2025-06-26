package com.easyBase.common.config;

import org.springframework.core.Ordered;

/**
 * Base interface for module configurations
 * Provides common functionality for all module configs
 */
public interface ModuleConfiguration extends Ordered {

    /**
     * Get module name
     */
    String getModuleName();

    /**
     * Initialize module configuration
     * Called after all beans are created
     */
    default void initialize() {
        // Override if initialization needed
    }

    /**
     * Validate module configuration
     * @throws ConfigurationException if validation fails
     */
    default void validate() throws ConfigurationException {
        // Override if validation needed
    }

    /**
     * Check if module is enabled
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Get module initialization order
     * Lower values initialize first
     */
    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Configuration exception for validation failures
     */
    class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super(message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}