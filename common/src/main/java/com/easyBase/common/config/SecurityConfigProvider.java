package com.easyBase.common.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Interface for Security Configuration Provider
 * Allows security module to provide configuration without circular dependencies
 */
public interface SecurityConfigProvider {

    /**
     * Provides the main security filter chain
     */
    SecurityFilterChain securityFilterChain();

    /**
     * Provides password encoder for the application
     */
    PasswordEncoder passwordEncoder();

    /**
     * Provides authentication manager
     */
    AuthenticationManager authenticationManager();

    /**
     * Provides CORS configuration source
     */
    CorsConfigurationSource corsConfigurationSource();

    /**
     * Check if a path should be public (no auth required)
     */
    boolean isPublicPath(String path);

    /**
     * Get JWT token header name
     */
    String getJwtHeader();

    /**
     * Get JWT token prefix
     */
    String getJwtPrefix();
}