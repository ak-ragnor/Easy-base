package com.easybase.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/easy-base/api/auth/**").permitAll()
						.requestMatchers("/easy-base/api/system/**").permitAll()
						.anyRequest().permitAll())
				.headers(headers -> headers
						.frameOptions(
								HeadersConfigurer.FrameOptionsConfig::deny)
						.contentTypeOptions(contentType -> {
						})
						.httpStrictTransportSecurity(hstsConfig -> hstsConfig
								.maxAgeInSeconds(31536000)
								.includeSubDomains(true)));

		return http.build();
	}
}
