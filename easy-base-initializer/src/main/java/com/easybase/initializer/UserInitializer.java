/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.common.exception.ConflictException;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.repository.UserRoleRepository;
import com.easybase.core.role.service.RoleLocalService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;
import com.easybase.core.user.service.UserLocalService;
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

		_createAdminUser();
		_createGuestUser();
	}

	private void _createAdminUser() {
		log.info("Checking for default admin user...");

		Tenant defaultTenant = _tenantLocalService.getDefaultTenant();

		if (_userRepository.existsByEmailAndTenantId(
				_adminEmail, defaultTenant.getId())) {

			log.info("Default admin user already exists: {}", _adminEmail);
			log.info("Default user initialization completed");

			return;
		}

		try {
			User adminUser = _userService.createUser(
				_adminEmail, "Admin", "User", "Administrator",
				defaultTenant.getId());

			_userService.updatePasswordCredential(
				adminUser.getId(), _adminPassword);

			Role adminRole = _roleLocalService.getRoleByName(
				SystemRoles.ADMIN, defaultTenant.getId());

			UserRole userRole = new UserRole(
				adminUser.getId(), adminRole.getId(), defaultTenant.getId());

			_userRoleRepository.save(userRole);

			log.info(
				"Created default admin user: {} with ADMIN role", _adminEmail);
		}
		catch (ConflictException conflictException) {
			log.info(
				"Default admin user was created concurrently: {}", _adminEmail);
		}
		catch (Exception exception) {
			log.error("Failed to create default admin user", exception);

			throw exception;
		}

		log.info("Default user initialization completed");
	}

	private void _createGuestUser() {
		log.info("Checking for default guest user...");

		Tenant defaultTenant = _tenantLocalService.getDefaultTenant();

		if (_userRepository.existsByEmailAndTenantId(
				_guestEmail, defaultTenant.getId())) {

			log.info("Default guest user already exists: {}", _guestEmail);

			return;
		}

		try {
			User guestUser = _userService.createUser(
				_guestEmail, "Guest", "User", "Guest", defaultTenant.getId());

			Role guestRole = _roleLocalService.getRoleByName(
				SystemRoles.GUEST, null);

			UserRole userRole = new UserRole(
				guestUser.getId(), guestRole.getId(), defaultTenant.getId());

			_userRoleRepository.save(userRole);

			log.info(
				"Created default guest user: {} with GUEST role", _guestEmail);
		}
		catch (ConflictException conflictException) {
			log.info(
				"Default guest user was created concurrently: {}", _guestEmail);
		}
		catch (Exception exception) {
			log.error("Failed to create default guest user", exception);

			throw exception;
		}
	}

	@Value("${easy-base.admin.email:admin@easybase.com}")
	private String _adminEmail;

	@Value("${easy-base.admin.password:admin123}")
	private String _adminPassword;

	@Value("${easy-base.guest.email:guest@easybase.com}")
	private String _guestEmail;

	private final RoleLocalService _roleLocalService;
	private final TenantLocalService _tenantLocalService;
	private final UserRepository _userRepository;
	private final UserRoleRepository _userRoleRepository;
	private final UserLocalService _userService;

}