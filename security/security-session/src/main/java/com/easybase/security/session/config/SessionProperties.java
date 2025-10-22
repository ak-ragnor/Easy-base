/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.config;

import java.time.Duration;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Akhash
 */
@ConfigurationProperties(prefix = "easy.base.security.session")
@Data
public class SessionProperties {

	@SuppressWarnings("unused")
	private Duration cleanupGracePeriod = Duration.ofDays(7);

	@SuppressWarnings("unused")
	private Duration cleanupInterval = Duration.ofHours(1);

	@SuppressWarnings("unused")
	private Duration defaultTtl = Duration.ofDays(30);

	@SuppressWarnings("unused")
	private int maxSessionsPerUser = 5;

	@SuppressWarnings("unused")
	private boolean slidingExpiration = true;

}