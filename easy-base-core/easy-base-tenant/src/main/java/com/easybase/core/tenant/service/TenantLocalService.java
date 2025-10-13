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
 * Local service interface for tenant business logic and data operations.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of TenantService.
 *
 * @author Akhash R
 */
public interface TenantLocalService {

	/**
	 * Creates a new tenant with the given name.
	 *
	 * @param name the tenant name
	 * @return the created tenant
	 * @throws com.easybase.common.exception.ConflictException if name already exists
	 */
	Tenant createTenant(String name);

	/**
	 * Soft deletes a tenant by ID.
	 *
	 * @param tenantId the tenant ID
	 * @throws com.easybase.common.exception.ResourceNotFoundException if tenant not found
	 */
	void deleteTenant(UUID tenantId);

	/**
	 * Fetches a tenant by name (optional result).
	 *
	 * @param name the tenant name
	 * @return Optional containing the tenant if found
	 */
	Optional<Tenant> fetchTenant(String name);

	/**
	 * Gets or creates the default tenant.
	 *
	 * @return the default tenant
	 */
	Tenant getDefaultTenant();

	/**
	 * Gets a tenant by name.
	 *
	 * @param name the tenant name
	 * @return the tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	Tenant getTenant(String name);

	/**
	 * Gets a tenant by ID.
	 *
	 * @param id the tenant ID
	 * @return the tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	Tenant getTenant(UUID id);

	/**
	 * Gets all tenants.
	 *
	 * @return list of all tenants
	 */
	List<Tenant> getTenants();

	/**
	 * Updates a tenant's name.
	 *
	 * @param id the tenant ID
	 * @param name the new name
	 * @return the updated tenant
	 * @throws com.easybase.common.exception.ResourceNotFoundException if tenant not found
	 * @throws com.easybase.common.exception.ConflictException if name already exists
	 */
	Tenant updateTenant(UUID id, String name);

}