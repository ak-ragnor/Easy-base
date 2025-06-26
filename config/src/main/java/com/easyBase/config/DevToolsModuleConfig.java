package com.easyBase.config;

import com.easyBase.common.config.ModuleConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.annotation.PostConstruct;

/**
 * Development Tools Module Configuration
 * This is the first module to be migrated out of the central config
 * Only active in development profile
 */
@Configuration
@Profile("dev")
@ConditionalOnProperty(name = "devtools.enabled", havingValue = "true", matchIfMissing = true)
public class DevToolsModuleConfig implements ModuleConfiguration {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${devtools.request-logging.include-query-string:true}")
    private boolean includeQueryString;

    @Value("${devtools.request-logging.include-payload:true}")
    private boolean includePayload;

    @Value("${devtools.request-logging.include-headers:false}")
    private boolean includeHeaders;

    @Value("${devtools.request-logging.max-payload-length:1000}")
    private int maxPayloadLength;

    @Override
    public String getModuleName() {
        return "DevTools";
    }

    @Override
    public int getOrder() {
        // Load after core configs but before application modules
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

    @Override
    public boolean isEnabled() {
        return "dev".equals(activeProfile);
    }

    @PostConstruct
    public void initialize() {
        System.out.println("=== DevTools Module Configuration ===");
        System.out.println("Module: " + getModuleName());
        System.out.println("Active Profile: " + activeProfile);
        System.out.println("Request Logging Enabled: true");
        System.out.println("Include Query String: " + includeQueryString);
        System.out.println("Include Payload: " + includePayload);
        System.out.println("Include Headers: " + includeHeaders);
        System.out.println("===================================");
    }

    @Override
    public void validate() throws ConfigurationException {
        if (maxPayloadLength < 0) {
            throw new ConfigurationException("Max payload length cannot be negative");
        }

        if (maxPayloadLength > 10000) {
            System.out.println("WARNING: Large max payload length may impact performance: " + maxPayloadLength);
        }
    }

    /**
     * Request logging filter for development
     * Logs all incoming HTTP requests
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setIncludeQueryString(includeQueryString);
        filter.setIncludePayload(includePayload);
        filter.setIncludeHeaders(includeHeaders);
        filter.setMaxPayloadLength(maxPayloadLength);
        filter.setIncludeClientInfo(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");

        return filter;
    }

    /**
     * Development-only bean for debugging
     * Shows all registered beans on startup
     */
    @Bean
    @ConditionalOnProperty(name = "devtools.show-beans", havingValue = "true")
    public BeanDebugger beanDebugger() {
        return new BeanDebugger();
    }

    /**
     * Inner class for bean debugging
     */
    public static class BeanDebugger {
        @PostConstruct
        public void showBeans() {
            System.out.println("=== Bean Debugger Active ===");
            System.out.println("To see all beans, enable trace logging for Spring context");
            System.out.println("===========================");
        }
    }
}