/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.tenant.service.impl;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;
import com.easybase.core.tenant.service.TenantLocalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link TenantLocalService}.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TenantLocalServiceImpl implements TenantLocalService {

	@Transactional
	public Tenant createTenant(String name) {
		boolean exists = _tenantRepository.existsByName(name);

		if (exists) {
			throw new ConflictException("Tenant", "name", name);
		}

		Tenant tenant = new Tenant();

		tenant.setName(name);

		return _tenantRepository.save(tenant);
	}

	@Transactional
	public void deleteTenant(UUID tenantId) {
		Optional<Tenant> tenantOptional = _tenantRepository.findById(tenantId);

		if (tenantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Tenant", "id", tenantId);
		}

		Tenant tenant = tenantOptional.get();

		tenant.setDeleted(true);

		_tenantRepository.save(tenant);

		log.info("Soft deleted tenant '{}' ({})", tenant.getName(), tenantId);
	}

	public Optional<Tenant> fetchTenant(String name) {
		return _tenantRepository.findByName(name);
	}

	@Transactional
	public Tenant getDefaultTenant() {
		Optional<Tenant> tenantOptional = fetchTenant("default");

		if (tenantOptional.isEmpty()) {
			try {
				return createTenant("default");
			}
			catch (ConflictException conflictException) {
				tenantOptional = fetchTenant("default");

				if (tenantOptional.isEmpty()) {
					throw conflictException;
				}
			}
		}

		return tenantOptional.get();
	}

	public Tenant getTenant(String name) {
		Optional<Tenant> tenantOptional = _tenantRepository.findByName(name);

		if (tenantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Tenant", "name", name);
		}

		return tenantOptional.get();
	}

	public Tenant getTenant(UUID id) {
		Optional<Tenant> tenantOptional = _tenantRepository.findById(id);

		if (tenantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Tenant", "id", id);
		}

		return tenantOptional.get();
	}

	public List<Tenant> getTenants() {
		return _tenantRepository.findAll();
	}

	@Transactional
	public Tenant updateTenant(UUID id, String name) {
		boolean exists = _tenantRepository.existsByName(name);

		Tenant tenant = getTenant(id);

		String tenantName = tenant.getName();

		if (exists && !tenantName.equals(name)) {
			throw new ConflictException("Tenant", "name", name);
		}

		tenant.setName(name);

		tenant = _tenantRepository.save(tenant);

		log.info("Updated tenant with ID {} to name '{}'", id, name);

		return tenant;
	}

	private final TenantRepository _tenantRepository;

}