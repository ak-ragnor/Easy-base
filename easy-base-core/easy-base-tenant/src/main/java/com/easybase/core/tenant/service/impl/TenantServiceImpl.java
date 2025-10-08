/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.tenant.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.tenant.action.TenantActions;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.core.tenant.service.TenantService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link TenantService}.
 * ALWAYS performs permission checks before delegating to TenantLocalService.
 * Never performs persistence directly - always delegates to TenantLocalService.
 *
 * <p>If permission checks are not needed, use TenantLocalService directly.</p>
 *
 * @author Akhash R
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

	@Override
	public Tenant createTenant(String name) {
		_permissionChecker.check(TenantActions.CREATE);

		return _tenantLocalService.createTenant(name);
	}

	@Override
	public void deleteTenant(UUID tenantId) {
		_permissionChecker.check(TenantActions.DELETE);

		_tenantLocalService.deleteTenant(tenantId);
	}

	@Override
	public Optional<Tenant> fetchTenant(String name) {
		_permissionChecker.check(TenantActions.VIEW);

		return _tenantLocalService.fetchTenant(name);
	}

	@Override
	public Tenant getDefaultTenant() {
		_permissionChecker.check(TenantActions.VIEW);

		return _tenantLocalService.getDefaultTenant();
	}

	@Override
	public Tenant getTenant(String name) {
		_permissionChecker.check(TenantActions.VIEW);
		return _tenantLocalService.getTenant(name);
	}

	@Override
	public Tenant getTenant(UUID id) {
		_permissionChecker.check(TenantActions.VIEW);
		return _tenantLocalService.getTenant(id);
	}

	@Override
	public List<Tenant> getTenants() {
		_permissionChecker.check(TenantActions.LIST);
		return _tenantLocalService.getTenants();
	}

	@Override
	public Tenant updateTenant(UUID id, String name) {
		_permissionChecker.check(TenantActions.UPDATE);
		return _tenantLocalService.updateTenant(id, name);
	}

	private final PermissionChecker _permissionChecker;
	private final TenantLocalService _tenantLocalService;

}
