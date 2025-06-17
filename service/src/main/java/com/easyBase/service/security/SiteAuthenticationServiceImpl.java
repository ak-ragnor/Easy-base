package com.easyBase.service.security;

import com.easyBase.common.dto.auth.SiteLoginResponse;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.site.SiteRepository;
import com.easyBase.domain.repository.jpa.site.UserSiteRepository;
import com.easyBase.domain.repository.jpa.user.UserRepository;
import com.easyBase.security.jwt.SiteJwtTokenProvider;
import com.easyBase.service.exception.ResourceNotFoundException;
import com.easyBase.service.exception.UnauthorizedException;
import com.easyBase.service.mapper.SiteMapper;
import com.easyBase.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Site Authentication Service Implementation
 * Handles site-specific user authentication and authorization
 */
@Service
@Transactional
public class SiteAuthenticationServiceImpl implements SiteAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private SiteJwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SiteMapper siteMapper;

    @Override
    public SiteLoginResponse authenticateUserForSite(Long siteId, String email, String password) {
        _log.debug("Authenticating user {} for site {}", email, siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        if (site.getStatus() != SiteStatus.ACTIVE) {
            throw new UnauthorizedException("Site is not active");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("User account is not active");
        }

        UserSite userSite = userSiteRepository.findByUserIdAndSiteId(user.getId(), site.getId())
                .orElseThrow(() -> new UnauthorizedException("User not authorized for this site"));

        if (!userSite.getIsActive()) {
            throw new UnauthorizedException("User access to this site has been revoked");
        }

        String token = jwtTokenProvider.generateSiteToken(user, site, userSite);

        return _buildLoginResponse(user, site, userSite, token);
    }

    @Override
    public SiteLoginResponse authenticateUserForSite(String siteCode, String email, String password) {
        _log.debug("Authenticating user {} for site code {}", email, siteCode);

        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with code: " + siteCode));

        return authenticateUserForSite(site.getId(), email, password);
    }

    @Override
    public void validateUserSiteAccess(Long userId, Long siteId) throws UnauthorizedException {
        boolean hasAccess = userSiteRepository.existsByUserIdAndSiteIdAndIsActive(userId, siteId, true);

        if (!hasAccess) {
            throw new UnauthorizedException("User not authorized for this site");
        }
    }

    @Override
    public boolean isUserAuthorizedForSite(Long userId, Long siteId) {
        return userSiteRepository.existsByUserIdAndSiteIdAndIsActive(userId, siteId, true);
    }

    /**
     * Build login response DTO
     */
    private SiteLoginResponse _buildLoginResponse(User user, Site site, UserSite userSite, String token) {
        SiteLoginResponse response = new SiteLoginResponse();

        response.setSuccess(true);
        response.setMessage("Authentication successful");
        response.setToken(token);
        response.setUser(userMapper.toDTO(user));
        response.setSite(siteMapper.toDTO(site));
        response.setSiteRole(userSite.getRole());

        response.setExpiresAt(ZonedDateTime.now().plusSeconds(jwtTokenProvider.getJwtExpiration() / 1000));
        response.setSessionId(_extractSessionId(token));

        return response;
    }

    /**
     * Extract session ID from token
     */
    private String _extractSessionId(String token) {
        try {
            return jwtTokenProvider.validateTokenAndGetClaims(token).getSessionId();
        } catch (Exception e) {
            return "session-" + System.currentTimeMillis();
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(SiteAuthenticationServiceImpl.class);
}