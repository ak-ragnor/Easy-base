/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.web.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (Cross-Origin Resource Sharing) configuration
 *
 * @author Akhash R
 */
@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();

        // Allow requests from frontend development servers
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite default port
            "http://localhost:5174",  // Vite alternate port
            "http://localhost:3000"   // Common React/Next.js port
        ));

		// Allow all HTTP methods

		config.setAllowedMethods(
			Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

		// Allow all headers

		config.setAllowedHeaders(List.of("*"));

		// Allow credentials (cookies, authorization headers with Bearer tokens)

		config.setAllowCredentials(true);

		// Expose these headers to the frontend

		config.setExposedHeaders(
			Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));

		// How long (in seconds) the browser can cache preflight responses

		config.setMaxAge(3600L);

		// Register CORS configuration for all endpoints

		UrlBasedCorsConfigurationSource source =
			new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", config);

		return new CorsFilter(source);
	}

}