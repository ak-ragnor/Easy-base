/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test class for tests that require PostgreSQL database
 * @author Akhash
 */
@Testcontainers
public class PostgreSQLTestBase {

	@Container
	private static final PostgreSQLContainer<?> _postgresContainer =
		new PostgreSQLContainer<>(
			"postgres:15-alpine"
		).withDatabaseName(
			"easybase_test"
		).withUsername(
			"test"
		).withPassword(
			"test"
		);

	@BeforeAll
	static void _startContainer() {
		_postgresContainer.start();
	}

	@AfterAll
	static void _stopContainer() {
		_postgresContainer.stop();
	}

	@DynamicPropertySource
	static void _configureTestDatabase(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", _postgresContainer::getJdbcUrl);
		registry.add(
			"spring.datasource.username", _postgresContainer::getUsername);
		registry.add(
			"spring.datasource.password", _postgresContainer::getPassword);
	}

}