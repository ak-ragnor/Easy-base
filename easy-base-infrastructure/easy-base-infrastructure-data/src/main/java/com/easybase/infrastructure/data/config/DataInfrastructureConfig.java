/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Central configuration for data infrastructure layer.
 * This configuration handles JPA setup, entity scanning, repository configuration,
 * auditing, and transaction management for the entire application.
 *
 * The configuration is modular and can be conditionally enabled based on
 * application properties.
 *
 * @author Akhash R
 */
@ConditionalOnProperty(
	havingValue = "true", matchIfMissing = true,
	name = "easybase.data.infrastructure.enabled"
)
@Configuration
public class DataInfrastructureConfig {

	/**
	 * Configuration for JPA repositories and entity scanning.
	 * Enables JPA repositories across the entire com.easybase package tree
	 * and scans for entities in the same scope.
	 */
	@Configuration
	@EnableJpaRepositories(
		basePackages = {
			"com.easybase.core",           // Domain repositories
			"com.easybase.system",         // System repositories
			"com.easybase.security",       // Security repositories
			"com.easybase.infrastructure"  // Infrastructure repositories
		}
	)
	@EntityScan(
		basePackages = {
			"com.easybase.core",           // Domain entities
			"com.easybase.system",         // System entities
			"com.easybase.security",       // Security entities
			"com.easybase.common.data"     // Common data entities
		}
	)
	static class RepositoryConfig {
	}

	/**
	 * Configuration for JPA auditing features.
	 * Enables automatic population of audit fields like created/updated timestamps.
	 */
	@Configuration
	@EnableJpaAuditing
	static class AuditingConfig {
	}

	/**
	 * Configuration for transaction management.
	 * Enables declarative transaction management using @Transactional annotations.
	 */
	@Configuration
	@EnableTransactionManagement
	static class TransactionConfig {
	}

}