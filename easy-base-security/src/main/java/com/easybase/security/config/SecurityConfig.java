/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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