package com.easyBase.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * NoOp Authentication Manager for JWT-based authentication
 * This is used because Spring Security requires an AuthenticationManager
 * but our JWT filter handles the actual authentication
 */
public class NoOpAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        throw new BadCredentialsException("Authentication is handled by JWT filter");
    }
}