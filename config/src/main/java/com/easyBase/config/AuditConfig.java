package com.easyBase.config;

import com.easyBase.domain.entity.base.AuditorAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditAwareImpl() {
        return new AuditorAwareImpl();
    }
}