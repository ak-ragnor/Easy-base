/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.common.exception.ConflictException;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.repository.RoleRepository;
import com.easybase.core.role.repository.UserRoleRepository;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;
import com.easybase.core.user.service.UserService;
import com.easybase.infrastructure.auth.constants.SystemRoles;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for creating default admin user.
 * Runs fourth, after action discovery.
 *
 * @author Akhash R
 */
@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class UserInitializer implements ApplicationRunner {

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		log.info("=== Step 4: Default User Initialization ===");
		createDefaultAdminUser();
	}

	private void createDefaultAdminUser() {
		log.info("Checking for default admin user...");

		Tenant defaultTenant = _tenantService.getDefaultTenant();

		// Check if admin user already exists

		if (_userRepository.existsByEmailAndTenantId(
				_adminEmail, defaultTenant.getId())) {

			log.info("Default admin user already exists: {}", _adminEmail);
			log.info("Default user initialization completed");

			return;
		}

		try {

			// Create admin user

			User adminUser = _userService.createUser(
				_adminEmail, "Admin", "User", "Administrator",
				defaultTenant.getId());

			// Update password to configured admin password

			_userService.updatePasswordCredential(
				adminUser.getId(), _adminPassword);

			// Get ADMIN role

			Role adminRole = _roleRepository.findByNameAndSystemTrue(
				SystemRoles.ADMIN
			).orElseThrow(
				() -> new IllegalStateException(
					"ADMIN role not found. Ensure DefaultRolesInitializer ran successfully.")
			);

			// Assign ADMIN role to user

			UserRole userRole = new UserRole(
				adminUser.getId(), adminRole.getId(), defaultTenant.getId());

			_userRoleRepository.save(userRole);

			log.info(
				"Created default admin user: {} with ADMIN role", _adminEmail);
		}
		catch (ConflictException e) {

			// Race condition - another instance created the user

			log.info(
				"Default admin user was created concurrently: {}", _adminEmail);
		}
		catch (Exception e) {
			log.error("Failed to create default admin user", e);

			throw e;
		}

		log.info("Default user initialization completed");
	}

	@Value("${easy-base.admin.email:admin@easybase.com}")
	private String _adminEmail;

	@Value("${easy-base.admin.password:admin123}")
	private String _adminPassword;

	private final RoleRepository _roleRepository;
	private final TenantService _tenantService;
	private final UserRepository _userRepository;
	private final UserRoleRepository _userRoleRepository;
	private final UserService _userService;

}