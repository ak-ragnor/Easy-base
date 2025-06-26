package com.easyBase.common.config;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Interface for Web MVC Configuration Provider
 * Allows web module to provide configuration without circular dependencies
 */
public interface WebConfigProvider {

    /**
     * Configure HTTP message converters
     */
    void configureMessageConverters(List<HttpMessageConverter<?>> converters);

    /**
     * Provide Jackson ObjectMapper configuration
     */
    ObjectMapper objectMapper();

    /**
     * Configure interceptors
     */
    void configureInterceptors(InterceptorRegistry registry);

    /**
     * Configure CORS mappings
     */
    void configureCors(CorsRegistry registry);

    /**
     * Provide validator instance
     */
    Validator validator();

    /**
     * Get API base path
     */
    String getApiBasePath();

    /**
     * Get API version
     */
    String getApiVersion();

    /**
     * Check if request/response logging is enabled
     */
    boolean isRequestLoggingEnabled();
}