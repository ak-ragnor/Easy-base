/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.core.role.domain.entity.Role;
import com.easybase.core.role.domain.entity.UserRole;
import com.easybase.core.role.infrastructure.presistence.repository.UserRoleRepository;
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
 * Initializer for creating default users.
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

		Tenant tenant = _tenantLocalService.getDefaultTenant();

		_createAdminUser(tenant);
		_createGuestUser(tenant);

		log.info("Default user initialization completed");
	}

	private void _assignRoleIfAbsent(
		User user, String roleName, Tenant tenant, String roleDisplayName) {

		try {
			Role role = _roleLocalService.getRoleByName(
				roleName, tenant.getId());

			if (_userRoleRepository.existsByUserIdAndRoleId(
					user.getId(), role.getId())) {

				log.info(
					"User already has {} role: {}", roleDisplayName,
					user.getEmail());

				return;
			}

			UserRole userRole = new UserRole(
				user.getId(), role.getId(), tenant.getId());

			_userRoleRepository.save(userRole);

			log.info(
				"Assigned {} role to user: {}", roleDisplayName,
				user.getEmail());
		}
		catch (Exception exception) {
			log.error(
				"Failed to assign {} role to user: {}", roleDisplayName,
				user.getEmail(), exception);

			throw exception;
		}
	}

	private void _createAdminUser(Tenant defaultTenant) {
		log.info("Checking for default admin user...");

		User adminUser = _getOrCreateUser(
			_adminEmail, "Admin", "User", "Administrator", defaultTenant, true);

		_assignRoleIfAbsent(
			adminUser, SystemRoles.ADMIN, defaultTenant, "ADMIN");
	}

	private void _createGuestUser(Tenant defaultTenant) {
		log.info("Checking for default guest user...");

		User guestUser = _getOrCreateUser(
			_guestEmail, "Guest", "User", "Guest", defaultTenant, false);

		if (guestUser != null) {
			_assignRoleIfAbsent(
				guestUser, SystemRoles.GUEST, defaultTenant, "GUEST");
		}
	}

	private User _getOrCreateUser(
		String email, String firstName, String lastName, String screenName,
		Tenant tenant, boolean setPassword) {

		if (_userRepository.existsByEmailAndTenantId(email, tenant.getId())) {
			log.info("Default user already exists: {}", email);

			return _userLocalService.getUser(email, tenant.getId());
		}

		try {
			User user = _userLocalService.createUser(
				email, firstName, lastName, screenName, tenant.getId());

			if (setPassword) {
				_userLocalService.updatePasswordCredential(
					user.getId(), _adminPassword);
			}

			log.info("Created default user: {}", email);

			return user;
		}
		catch (Exception exception) {
			log.error("Failed to create default user: {}", email, exception);

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
	private final UserLocalService _userLocalService;
	private final UserRepository _userRepository;
	private final UserRoleRepository _userRoleRepository;

}