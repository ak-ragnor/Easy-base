/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.core.role.entity.Role;
import com.easybase.core.role.repository.RoleRepository;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.infrastructure.auth.constants.SystemRoles;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for creating default system roles.
 * Runs second, after default tenant.
 *
 * @author Akhash R
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class RolesInitializer implements ApplicationRunner {

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		log.info("=== Step 2: Default Roles Initialization ===");
		createDefaultRoles();
	}

	private void createDefaultRoles() {
		log.info("Creating default system roles...");

		createRoleIfNotExists(
			SystemRoles.ADMIN, "Administrator role with full system access");

		createRoleIfNotExists(
			SystemRoles.USER, "Standard authenticated user role");

		createRoleIfNotExists(
			SystemRoles.GUEST, "Guest role with limited read-only permissions");

		log.info("Default roles initialization completed");
	}

	private void createRoleIfNotExists(String name, String description) {
		if (!_roleRepository.existsByNameAndSystemTrue(name)) {
			Role role = new Role();

			role.setName(name);
			role.setDescription(description);
			role.setSystem(true);
			role.setActive(true);

			Tenant tenant = _tenantLocalService.getDefaultTenant();

			role.setTenantId(tenant.getId());

			_roleRepository.save(role);
			log.info("Created system role: {}", name);
		}
		else {
			log.debug("System role already exists: {}", name);
		}
	}

	private final RoleRepository _roleRepository;
	private final TenantLocalService _tenantLocalService;

}