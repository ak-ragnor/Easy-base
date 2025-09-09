/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.starter;

import com.easybase.security.api.service.AuthenticationFacade;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.jwt.config.JwtProperties;
import com.easybase.security.session.config.SessionProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Akhash
 */
@AutoConfiguration
@ComponentScan(
	basePackages = {
		"com.easybase.security.core", "com.easybase.security.jwt",
		"com.easybase.security.session", "com.easybase.security.web"
	}
)
@ConditionalOnClass(
	{TokenService.class, SessionService.class, AuthenticationFacade.class}
)
@ConditionalOnProperty(
	matchIfMissing = true, name = "enabled", prefix = "easy.base.security"
)
@EnableConfigurationProperties({JwtProperties.class, SessionProperties.class})
public class EasyBaseSecurityAutoConfiguration {
}