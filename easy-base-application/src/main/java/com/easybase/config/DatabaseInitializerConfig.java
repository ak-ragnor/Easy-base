/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.service.UserLocalService;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for database initialization.
 * Handles creation of default tenant and admin user on application startup.
 *
 * @author Akhash R
 */
@ConditionalOnProperty(
	havingValue = "true", matchIfMissing = true,
	name = "easy-base.initialization.enabled"
)
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializerConfig {

	@PostConstruct
	public void initializeDefaultData() {
		try {
			Tenant tenant = _tenantLocalService.getDefaultTenant();

			log.info("Default tenant initialized: {}", tenant.getName());

			try {
				_userService.getUser(_adminEmail, tenant.getId());
				log.info("Admin user already exists: {}", _adminEmail);
			}
			catch (Exception exception) {
				User adminUser = _userService.createUser(
					_adminEmail, _adminFirstName, _adminLastName,
					_adminDisplayName, tenant.getId());

				_userService.updatePasswordCredential(
					adminUser.getId(), _adminPassword);

				log.info(
					"Admin user created successfully: {} (ID: {})", _adminEmail,
					adminUser.getId());
			}
		}
		catch (Exception exception) {
			log.error(
				"Failed to initialize default data: {}", exception.getMessage(),
				exception);
		}
	}

	@Value("${easy-base.initialization.admin-user.display-name:Administrator}")
	private String _adminDisplayName;

	@Value("${easy-base.initialization.admin-user.email:admin@easybase.com}")
	private String _adminEmail;

	@Value("${easy-base.initialization.admin-user.first-name:Admin}")
	private String _adminFirstName;

	@Value("${easy-base.initialization.admin-user.last-name:User}")
	private String _adminLastName;

	@Value("${easy-base.initialization.admin-user.password:admin123}")
	private String _adminPassword;

	private final TenantLocalService _tenantLocalService;
	private final UserLocalService _userService;

}