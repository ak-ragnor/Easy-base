/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.config;

import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
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

	/**
	 * Configuration for JOOQ DSL context.
	 * Provides DSL context bean for type-safe SQL query building.
	 */
	@Configuration
	static class JooqConfig {

		@Bean
		public DSLContext dslContext(DataSource dataSource) {
			return DSL.using(dataSource, SQLDialect.POSTGRES);
		}

	}

	/**
	 * Configuration for database initialization.
	 * Handles database setup and extension installation.
	 */
	@Configuration
	static class DatabaseInitializerConfig {

		@PostConstruct
		public void initializeDatabaseExtensions() {
			_dslContext.execute("CREATE EXTENSION IF NOT EXISTS \"pgcrypto\";");
		}

		private final DSLContext _dslContext;

		DatabaseInitializerConfig(DSLContext dslContext) {
			_dslContext = dslContext;
		}

	}

}