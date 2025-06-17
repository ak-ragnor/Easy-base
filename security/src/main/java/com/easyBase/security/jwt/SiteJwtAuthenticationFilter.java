package com.easyBase.security.jwt;

import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.site.SiteRepository;
import com.easyBase.domain.repository.jpa.site.UserSiteRepository;
import com.easyBase.domain.repository.jpa.user.UserRepository;
import com.easyBase.security.context.ServiceContext;
import com.easyBase.security.context.ServiceContextHolder;
import com.easyBase.security.context.ServiceContextImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authentication Filter for Site-specific authentication
 * Processes JWT tokens and sets up ServiceContext
 */
@Component
public class SiteJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SiteJwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = _extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && !jwtTokenProvider.isTokenExpired(token)) {
                if (_validateAndSetupContext(token, request)) {
                    _log.debug("Successfully authenticated request for: {}", request.getRequestURI());
                } else {
                    _rejectRequest(response, "Authentication failed");
                    return;
                }
            }

        } catch (Exception e) {
            _log.error("Authentication error: {}", e.getMessage());
            _auditLog.warn("Authentication failure - IP: {}, URI: {}, Error: {}",
                    _getClientIpAddress(request), request.getRequestURI(), e.getMessage());
            _rejectRequest(response, "Authentication error");

            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Comprehensive Validation
     */
    private boolean _validateAndSetupContext(String token, HttpServletRequest request) {
        try {
            JwtTokenClaims claims = jwtTokenProvider.validateTokenAndGetClaims(token);

            User user = userRepository.findById(claims.getUserId()).orElse(null);
            if (user == null) {
                _auditLog.warn("User not found: {}", claims.getUserId());
                return false;
            }

            if (user.getStatus() != UserStatus.ACTIVE) {
                _auditLog.warn("Inactive user attempted access: {}", user.getEmail());
                return false;
            }

            Site site = siteRepository.findById(claims.getSiteId()).orElse(null);
            if (site == null) {
                _auditLog.warn("Site not found: {}", claims.getSiteId());
                return false;
            }

            if (site.getStatus() != SiteStatus.ACTIVE) {
                _auditLog.warn("Inactive site access attempted: {}", site.getCode());
                return false;
            }

            UserSite userSite = userSiteRepository.findByUserIdAndSiteId(
                    user.getId(), site.getId()).orElse(null);

            if (userSite == null || !userSite.getIsActive()) {
                _auditLog.warn("User {} not authorized for site {}", user.getEmail(), site.getCode());
                return false;
            }

            UserDetails userDetails = _createUserDetails(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            ServiceContext serviceContext = new ServiceContextImpl(user, site, userSite, claims);

            ServiceContextHolder.setContext(serviceContext);

            _log.debug("Authenticated user {} for site {} - URI: {}",
                    user.getEmail(), site.getCode(), request.getRequestURI());

            return true;

        } catch (Exception e) {
            _log.error("Context setup failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Reject request with proper error response
     */
    private void _rejectRequest(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message, java.time.ZonedDateTime.now()));
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String _extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Create UserDetails for Spring Security
     */
    private UserDetails _createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // Add authorities as needed
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Get client IP address, handling proxy headers
     */
    private String _getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.endsWith("/login") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/api/health/") ||
                path.startsWith("/api/info/");
    }

    private static final Logger _log = LoggerFactory.getLogger(SiteJwtAuthenticationFilter.class);
    private static final Logger _auditLog = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
}