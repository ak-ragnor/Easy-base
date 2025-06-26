package com.easyBase.security.config;

import com.easyBase.common.config.ConfigProviderRegistry;
import com.easyBase.common.config.ModuleConfiguration;
import com.easyBase.common.config.SecurityConfigProvider;
import com.easyBase.security.jwt.JwtAuthenticationEntryPoint;
import com.easyBase.security.jwt.JwtAuthenticationFilter;
import com.easyBase.security.authentication.DatabaseAuthenticationProvider;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Security Module Configuration
 * Contains all Spring Security related configuration
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan(basePackages = {"com.easyBase.security"})
public class SecurityModuleConfig implements ModuleConfiguration, SecurityConfigProvider {

    @Value("${security.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${security.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${security.auth.general-user-enabled:true}")
    private boolean generalUserEnabled;

    @Value("${security.jwt.header:Authorization}")
    private String jwtHeader;

    @Value("${security.jwt.prefix:Bearer }")
    private String jwtPrefix;

    @Value("${security.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${security.jwt.expiration:86400}")
    private long jwtExpiration;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private DatabaseAuthenticationProvider databaseAuthenticationProvider;

    @Autowired
    private ConfigProviderRegistry registry;

    @Override
    public String getModuleName() {
        return "Security";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10; // Load early
    }

    @PostConstruct
    public void initialize() {
        System.out.println("=== Security Module Configuration ===");
        System.out.println("Module: " + getModuleName());
        System.out.println("JWT Header: " + jwtHeader);
        System.out.println("JWT Expiration: " + jwtExpiration + " seconds");
        System.out.println("CORS Enabled: true");
        System.out.println("General User Login: " + generalUserEnabled);
        System.out.println("=====================================");

        // Register as SecurityConfigProvider
        registry.register(SecurityConfigProvider.class, this);
    }

    @Override
    public void validate() throws ConfigurationException {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new ConfigurationException("JWT secret must be at least 32 characters");
        }
        if (jwtExpiration < 300) { // 5 minutes
            throw new ConfigurationException("JWT expiration must be at least 300 seconds");
        }
    }

    // ========== Security Configuration Beans ==========

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(databaseAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF disabled for REST API
                .csrf(csrf -> csrf.disable())

                // Session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Swagger/API docs
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // User management
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ========== SecurityConfigProvider Implementation ==========

    @Override
    public SecurityFilterChain securityFilterChain() {
        try {
            return securityFilterChain(null); // HttpSecurity injected by Spring
        } catch (Exception e) {
            throw new RuntimeException("Failed to create security filter chain", e);
        }
    }

    @Override
    public boolean isPublicPath(String path) {
        return path != null && (
                path.startsWith("/api/auth/") ||
                        path.startsWith("/api/public/") ||
                        path.equals("/api/health") ||
                        path.startsWith("/static/") ||
                        path.startsWith("/webjars/") ||
                        path.contains("/swagger") ||
                        path.contains("/api-docs")
        );
    }

    @Override
    public String getJwtHeader() {
        return jwtHeader;
    }

    @Override
    public String getJwtPrefix() {
        return jwtPrefix;
    }

    // ========== Additional Security Beans ==========

    @Bean
    public JwtTokenConfiguration jwtTokenConfiguration() {
        return new JwtTokenConfiguration(jwtSecret, jwtExpiration, jwtHeader, jwtPrefix);
    }

    /**
     * Configuration holder for JWT settings
     */
    public static class JwtTokenConfiguration {
        private final String secret;
        private final long expiration;
        private final String header;
        private final String prefix;

        public JwtTokenConfiguration(String secret, long expiration, String header, String prefix) {
            this.secret = secret;
            this.expiration = expiration;
            this.header = header;
            this.prefix = prefix;
        }

        public String getSecret() { return secret; }
        public long getExpiration() { return expiration; }
        public String getHeader() { return header; }
        public String getPrefix() { return prefix; }
    }
}