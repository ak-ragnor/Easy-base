/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.config;

import com.easybase.security.web.filter.JwtSessionAuthenticationFilter;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration that defines JWT-based authentication
 * and authorization rules for the application.
 *
 * <p>This configuration sets up a stateless security filter chain with
 * JWT token authentication and defines public vs protected endpoints.</p>
 *
 * @author Akhash
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	/**
	 * Configures the security filter chain with JWT authentication
	 * and endpoint authorization rules.
	 *
	 * @param http the HttpSecurity to configure
	 * @return the configured SecurityFilterChain
	 * @throws Exception if configuration fails
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authz -> authz
				// Authentication endpoints - public
				.requestMatchers("/api/auth/login").permitAll()
				.requestMatchers("/api/auth/refresh").permitAll()

				// JWKS and health endpoints - public
				.requestMatchers("/.well-known/**").permitAll()
				.requestMatchers("/actuator/health").permitAll()

				// API documentation - public (can be restricted in production)
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

				// Temporarily allow all API endpoints for development/testing
				.requestMatchers("/easy-base/api/**").permitAll()

				// All other endpoints require authentication
				.anyRequest().authenticated()
			)
			.addFilterBefore(_jwtSessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(401);
					response.setContentType("application/json");

					String body =
						"{\"timestamp\":\"" + Instant.now() +
							"\",\"status\":401,\"error\":\"Unauthorized\"," +
								"\"message\":\"Authentication required\",\"path\":\"" +
									request.getRequestURI() + "\"}";

					response.getWriter(
					).write(
						body
					);
				}
			).accessDeniedHandler(
				(request, response, accessDeniedException) -> {
					response.setStatus(403);
					response.setContentType("application/json");

					String body =
						"{\"timestamp\":\"" + Instant.now() +
							"\",\"status\":403,\"error\":\"Forbidden\"," +
								"\"message\":\"Access denied\",\"path\":\"" +
									request.getRequestURI() + "\"}";

					response.getWriter(
					).write(
						body
					);
				}
			)
		).build();
	}

	/**
	 * Provides a BCrypt password encoder for secure password hashing.
	 *
	 * @return a BCrypt password encoder with strength 12
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	private final JwtSessionAuthenticationFilter
		_jwtSessionAuthenticationFilter;

}