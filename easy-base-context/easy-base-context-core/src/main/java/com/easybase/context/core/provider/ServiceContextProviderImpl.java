/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.provider;

import com.easybase.context.api.domain.CorrelationIds;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.domain.UserInfo;
import com.easybase.context.api.port.ServiceContextProvider;
import com.easybase.context.api.port.TenantInfoResolver;
import com.easybase.context.api.port.UserInfoResolver;
import com.easybase.context.core.impl.ServiceContextImpl;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ServiceContextProvider} that builds complete
 * ServiceContext from authenticated principal data.
 *
 * <p>This provider handles user resolution and tenant resolution to create
 * a comprehensive service context.</p>
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class ServiceContextProviderImpl implements ServiceContextProvider {

	@Override
	public ServiceContext build(Object principalData) {
		if (!(principalData instanceof AuthenticatedPrincipalData)) {
			return _buildGuestContext();
		}

		AuthenticatedPrincipalData principal =
			(AuthenticatedPrincipalData) principalData;

		UserInfo user = _resolveUser(principal);
		TenantInfo tenant = _resolveTenant(principal);
		CorrelationIds correlation = _buildCorrelation(principal);

		return ServiceContextImpl.builder()
			.user(user)
			.tenant(tenant)
			.correlation(correlation)
			.issuedAt(principal.getIssuedAt())
			.expiresAt(principal.getExpiresAt())
			.clientIp(principal.getClientIp())
			.userAgent(principal.getUserAgent())
			.build();
	}

	private CorrelationIds _buildCorrelation(
		AuthenticatedPrincipalData principal) {

		return CorrelationIds.create(
			principal.getSessionId(), null);
	}

	private TenantInfo _resolveTenant(
		AuthenticatedPrincipalData principalData) {

		try {
			return _tenantInfoResolver.resolve(principalData.getTenantId());
		}
		catch (Exception exception) {
			return new TenantInfo(
				principalData.getTenantId(), true,
				() -> "Tenant-" + principalData.getTenantId(), Map::of);
		}
	}

	private UserInfo _resolveUser(AuthenticatedPrincipalData principalData) {
		try {
			return _userInfoResolver.resolve(principalData.getUserId());
		}
		catch (Exception exception) {
			return UserInfo.anonymous();
		}
	}

	private ServiceContext _buildGuestContext() {
		Tenant tenant = _tenantLocalService.getDefaultTenant();

		TenantInfo tenantInfo = _tenantInfoResolver.resolve(
			tenant.getId());

		User guestUser = _userRepository.findActiveByEmailAndTenantId(
			_guestEmail, tenant.getId()
		).orElseThrow(
			() -> new IllegalStateException(
				"Guest user not found. Ensure UserInitializer ran successfully.")
		);

		UserInfo userInfo = _userInfoResolver.resolve(guestUser.getId());

		return ServiceContextImpl.builder()
			.user(userInfo)
			.tenant(tenantInfo)
			.correlation(CorrelationIds.create(null, null))
			.build();
	}

	private final TenantInfoResolver _tenantInfoResolver;
	private final TenantLocalService _tenantLocalService;
	private final UserInfoResolver _userInfoResolver;
	private final UserRepository _userRepository;

	@Value("${easy-base.guest.email:guest@easybase.com}")
	private String _guestEmail;

}