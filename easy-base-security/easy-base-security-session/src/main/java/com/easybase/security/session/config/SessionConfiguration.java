/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Akhash
 */
@Configuration
@EnableConfigurationProperties(SessionProperties.class)
@EnableScheduling
@EntityScan(basePackages = "com.easybase.security.session.entity")
public class SessionConfiguration {

	@Bean
	public ObjectMapper sessionObjectMapper() {
		return new ObjectMapper();
	}

}