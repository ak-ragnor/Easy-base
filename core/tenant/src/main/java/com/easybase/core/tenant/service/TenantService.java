/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.tenant.service;

import com.easybase.core.tenant.entity.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * External-facing service interface for tenant operations.
 * Performs permission checks before delegating to TenantLocalService.
 * Never performs persistence directly - always delegates to TenantLocalService.
 *
 * @author Akhash R
 */
public interface TenantService {

	/**
	 * Creates a new tenant with the given name.
	 * Requires TENANT:CREATE permission.
	 *
	 * @param name the tenant name
	 * @return the created tenant
	 * @throws com.easybase.common.exception.ConflictException if name already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Tenant createTenant(String name);

	/**
	 * Soft deletes a tenant by ID.
	 * Requires TENANT:DELETE permission.
	 *
	 * @param tenantId the tenant ID
	 * @throws com.easybase.common.exception.ResourceNotFoundException if tenant not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void deleteTenant(UUID tenantId);

	/**
	 * Fetches a tenant by name (optional result).
	 * No permission check - for internal use.
	 *
	 * @param name the tenant name
	 * @return Optional containing the tenant if found
	 */
	public Optional<Tenant> fetchTenant(String name);

	/**
	 * Gets or creates the default tenant.
	 * No permission check - for system use.
	 *
	 * @return the default tenant
	 */
	public Tenant getDefaultTenant();

	/**
	 * Gets a tenant by name.
	 * Requires TENANT:VIEW permission.
	 *
	 * @param name the tenant name
	 * @return the tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Tenant getTenant(String name);

	/**
	 * Gets a tenant by ID.
	 * Requires TENANT:VIEW permission.
	 *
	 * @param id the tenant ID
	 * @return the tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Tenant getTenant(UUID id);

	/**
	 * Gets all tenants.
	 * Requires TENANT:LIST permission.
	 *
	 * @return list of all tenants
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<Tenant> getTenants();

	/**
	 * Updates a tenant's name.
	 * Requires TENANT:UPDATE permission.
	 *
	 * @param id the tenant ID
	 * @param name the new name
	 * @return the updated tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if tenant not found
	 * @throws com.easybase.common.exception.ConflictException if name already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Tenant updateTenant(UUID id, String name);

}