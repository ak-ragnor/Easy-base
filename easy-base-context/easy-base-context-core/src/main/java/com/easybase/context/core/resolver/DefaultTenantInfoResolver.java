/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.resolver;

import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.port.AbstractDefaultResolver;
import com.easybase.context.api.port.TenantInfoResolver;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;
import com.easybase.core.tenant.service.TenantService;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link TenantInfoResolver} that resolves tenant information
 * from the database using TenantRepository. Uses AbstractDefaultResolver template
 * to eliminate code duplication.
 *
 * <p>This resolver provides access to tenant information with lazy loading
 * of tenant settings and handles default tenant fallbacks.</p>
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class DefaultTenantInfoResolver
	extends AbstractDefaultResolver<UUID, TenantInfo, Tenant>
	implements TenantInfoResolver {

	@Override
	protected TenantInfo createAnonymousInstance() {
		return toInfo(_tenantService.getDefaultTenant());
	}

	@Override
	protected String getEntityType() {
		return "Tenant";
	}

	@Override
	protected CrudRepository<Tenant, UUID> getRepository() {
		return _tenantRepository;
	}

	@Override
	protected TenantInfo toInfo(Tenant tenant) {
		UUID tenantId = tenant.getId();
		boolean active = isEntityActive(tenant);

		return new TenantInfo(
			tenantId, active, tenant::getName, () -> _extractSettings(tenant));
	}

	private Map<String, String> _extractSettings(Tenant tenant) {

		// TODO: Implement settings extraction

		return Map.of();
	}

	private final TenantRepository _tenantRepository;
	private final TenantService _tenantService;

}