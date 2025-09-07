/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Central configuration for web infrastructure layer.
 * This configuration handles web-related concerns like filters, context management,
 * and HTTP request processing for the entire application.
 *
 * The configuration is modular and can be conditionally enabled based on
 * application properties.
 *
 * @author Akhash R
 */
@ConditionalOnProperty(
	havingValue = "true", matchIfMissing = true,
	name = "easybase.web.infrastructure.enabled"
)
@Configuration
public class WebInfrastructureConfig {

	/**
	 * Configuration for service context and HTTP request processing.
	 * This will be populated with specific web configurations as they are moved
	 * from the application layer.
	 */

}