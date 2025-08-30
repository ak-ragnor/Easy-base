package com.easybase.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = { "com.easybase" })
@EntityScan(basePackages = { "com.easybase" })
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
	// Additional database configuration if needed
}
