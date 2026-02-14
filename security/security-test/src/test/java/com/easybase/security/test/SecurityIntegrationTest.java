/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.test;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;
import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.service.UserService;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.CreateSessionRequest;
import com.easybase.security.api.dto.LoginRequest;
import com.easybase.security.api.dto.Session;
import com.easybase.security.api.dto.TokenClaims;
import com.easybase.security.api.dto.TokenValidationResult;
import com.easybase.security.api.service.AuthenticationFacade;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.session.config.SessionProperties;

import java.time.Duration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the Security module
 * @author Akhash
 */
@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest
@TestPropertySource(
	properties = {
		"easy.base.security.session.max-sessions-per-user=2",
		"easy.base.security.session.sliding-expiration=true",
		"easy.base.security.session.default-ttl=PT1H",
		"easy.base.security.jwt.rotate-refresh-tokens=true"
	}
)
public class SecurityIntegrationTest extends PostgreSQLTestBase {

	@BeforeEach
	public void setUp() {
		String uuidString = String.valueOf(UUID.randomUUID());

		String shortUuid = uuidString.substring(0, 8);

		String tenantName = "Test Tenant " + shortUuid;

		Tenant tenant = tenantService.createTenant(tenantName);

		testTenantId = tenant.getId();

		testUser = userService.createUser(
			"test@example.com", "Test", "User", "Test User", testTenantId);
	}

	@Test
	@Transactional
	public void testFullAuthenticationFlow() {
		LoginRequest loginRequest = new LoginRequest();

		loginRequest.setUserName("test@example.com");
		loginRequest.setPassword("backToDust"); // Default password
		loginRequest.setTenantId(testTenantId);
		loginRequest.setClientIp("127.0.0.1");
		loginRequest.setUserAgent("Test-Agent");

		AuthenticatedPrincipalData principal =
			authenticationFacade.authenticateCredentials(loginRequest);

		Assertions.assertNotNull(principal);
		Assertions.assertEquals(testUser.getId(), principal.getUserId());
		Assertions.assertEquals(testTenantId, principal.getTenantId());
		Assertions.assertNotNull(principal.getSessionId());

		List<String> authorities = principal.getAuthorities();

		Assertions.assertTrue(authorities.contains("ROLE_USER"));
	}

	@Test
	@Transactional
	public void testInvalidCredentials() {
		LoginRequest loginRequest = new LoginRequest();

		loginRequest.setUserName("test@example.com");
		loginRequest.setPassword("wrongpassword");
		loginRequest.setTenantId(testTenantId);

		Assertions.assertThrows(
			ResourceNotFoundException.class,
			() -> authenticationFacade.authenticateCredentials(loginRequest));
	}

	@Test
	@Transactional
	public void testRefreshTokenFlow() {
		CreateSessionRequest sessionRequest = new CreateSessionRequest();

		sessionRequest.setUserId(testUser.getId());
		sessionRequest.setTenantId(testTenantId);

		Session session = sessionService.createSession(sessionRequest);

		String refreshToken = tokenService.generateRefreshToken(
			session.getSessionId());

		AuthenticatedPrincipalData refreshedPrincipal =
			authenticationFacade.refreshAuthentication(refreshToken);

		Assertions.assertNotNull(refreshedPrincipal);
		Assertions.assertEquals(
			testUser.getId(), refreshedPrincipal.getUserId());
		Assertions.assertEquals(testTenantId, refreshedPrincipal.getTenantId());
		Assertions.assertEquals(
			session.getSessionId(), refreshedPrincipal.getSessionId());
	}

	@Test
	@Transactional
	public void testSessionLimitEnforcement() {
		int maxSessions = sessionProperties.getMaxSessionsPerUser();

		Assertions.assertEquals(2, maxSessions); // As configured in test properties

		for (int i = 0; i < (maxSessions + 2); i++) {
			CreateSessionRequest sessionRequest = new CreateSessionRequest();

			sessionRequest.setUserId(testUser.getId());
			sessionRequest.setTenantId(testTenantId);
			sessionRequest.setClientIp("127.0.0.1");
			sessionRequest.setDeviceInfo("Device-" + i);

			sessionService.createSession(sessionRequest);
		}

		List<Session> activeSessions = sessionService.listSessions(
			testUser.getId(), testTenantId);

		Assertions.assertEquals(maxSessions, activeSessions.size());
	}

	@Test
	@Transactional
	public void testSessionRevocation() {
		CreateSessionRequest sessionRequest = new CreateSessionRequest();

		sessionRequest.setUserId(testUser.getId());
		sessionRequest.setTenantId(testTenantId);

		Session session = sessionService.createSession(sessionRequest);

		Optional<Session> retrievedSession = sessionService.getSession(
			session.getSessionId());

		Assertions.assertTrue(retrievedSession.isPresent());

		authenticationFacade.logout(session.getSessionId());

		Optional<Session> revokedSession = sessionService.getSession(
			session.getSessionId());

		Assertions.assertTrue(revokedSession.isEmpty());
	}

	@Test
	@Transactional
	public void testSlidingExpiration() {
		Assertions.assertTrue(sessionProperties.isSlidingExpiration()); // As configured in test properties

		CreateSessionRequest sessionRequest = new CreateSessionRequest();

		sessionRequest.setUserId(testUser.getId());
		sessionRequest.setTenantId(testTenantId);

		Session session = sessionService.createSession(sessionRequest);

		sessionService.touchSession(session.getSessionId());

		Optional<Session> updatedSession = sessionService.getSession(
			session.getSessionId());

		Assertions.assertTrue(updatedSession.isPresent());
	}

	@Test
	@Transactional
	public void testTokenGeneration() {
		CreateSessionRequest sessionRequest = new CreateSessionRequest();

		sessionRequest.setUserId(testUser.getId());
		sessionRequest.setTenantId(testTenantId);
		sessionRequest.setTtl(Duration.ofHours(1));

		Session session = sessionService.createSession(sessionRequest);

		AuthenticatedPrincipalData principal = new AuthenticatedPrincipalData();

		principal.setUserId(testUser.getId());
		principal.setTenantId(testTenantId);
		principal.setSessionId(session.getSessionId());
		principal.setAuthorities(List.of("ROLE_USER"));

		String accessToken = tokenService.generateAccessToken(
			principal, Duration.ofMinutes(15));

		String refreshToken = tokenService.generateRefreshToken(
			session.getSessionId());

		Assertions.assertNotNull(accessToken);
		Assertions.assertNotNull(refreshToken);

		TokenValidationResult result = tokenService.validateAccessToken(
			accessToken);

		Assertions.assertTrue(result.isValid());

		Optional<TokenClaims> claims = result.getClaims();

		UUID userId = claims.get(
		).getUserId();

		Assertions.assertEquals(testUser.getId(), userId);
	}

	@Autowired
	private AuthenticationFacade authenticationFacade;

	@Autowired
	private SessionProperties sessionProperties;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private TenantService tenantService;

	private UUID testTenantId;
	private User testUser;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserService userService;

}