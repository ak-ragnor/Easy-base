/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test configuration for Security Integration Tests
 * @author Akhash
 */
@EnableJpaRepositories(
	basePackages = {
		"com.easybase.core.user.repository",
		"com.easybase.core.tenant.repository"
	}
)
@EnableTransactionManagement
@EntityScan(
	basePackages = {
		"com.easybase.security.session.entity", "com.easybase.core.user.entity",
		"com.easybase.core.tenant.entity"
	}
)
@SpringBootApplication(
	scanBasePackages = {
		"com.easybase.security", "com.easybase.core", "com.easybase.context"
	}
)
public class TestConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(TestConfiguration.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}