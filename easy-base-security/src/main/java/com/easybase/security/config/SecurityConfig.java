/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Akhash R
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(
			AbstractHttpConfigurer::disable
		).sessionManagement(
			session -> session.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS)
		).authorizeHttpRequests(
			authz -> authz.requestMatchers(
				"/easy-base/api/auth/**"
			).permitAll(
			).requestMatchers(
				"/easy-base/api/system/**"
			).permitAll(
			).anyRequest(
			).permitAll()
		).headers(
			headers -> headers.frameOptions(
				HeadersConfigurer.FrameOptionsConfig::deny
			).contentTypeOptions(
				contentType -> {
				}
			).httpStrictTransportSecurity(
				hstsConfig -> hstsConfig.maxAgeInSeconds(
					31536000
				).includeSubDomains(
					true
				)
			)
		);

		return http.build();
	}

}