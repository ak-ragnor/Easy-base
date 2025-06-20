package com.easyBase.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("prod")
public class ProductionOptimizationConfig {

    @PostConstruct
    public void init() {

        // Production-specific optimizations

        System.setProperty("spring.jpa.open-in-view", "false");
        System.setProperty("spring.http.encoding.force", "true");
    }
}