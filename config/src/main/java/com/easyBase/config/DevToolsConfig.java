package com.easyBase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevToolsConfig {

    @Bean
    public RequestResponseLoggingFilter requestResponseLoggingFilter() {
        return new RequestResponseLoggingFilter();
    }

    @Bean
    public PerformanceMonitoringAspect performanceMonitoringAspect() {
        return new PerformanceMonitoringAspect();
    }
}