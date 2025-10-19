/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for authentication and authorization discovery.
 * Enables component scanning for action discovery and permission calculation.
 *
 * @author Akhash R
 */
@ComponentScan(basePackages = "com.easybase.core.auth")
@Configuration
@EnableCaching
public class AuthDiscoveryConfiguration {

	// Configuration is handled via annotations and component scanning
	// ActionDiscoveryService will be automatically detected and invoked via @PostConstruct

}