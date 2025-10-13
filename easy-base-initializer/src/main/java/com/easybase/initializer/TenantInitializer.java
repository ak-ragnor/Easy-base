/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for creating default tenant.
 * Runs first in the initialization sequence.
 *
 * @author Akhash R
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class TenantInitializer implements ApplicationRunner {

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		log.info("=== Step 1: Default Tenant Initialization ===");

		_createTenant();
	}

	private void _createTenant() {
		log.info("Checking for default tenant...");

		try {
			Tenant tenant = _tenantLocalService.getDefaultTenant();

			log.info("Default tenant already exists: {}", tenant.getName());
		}
		catch (Exception exception) {
			log.error("Failed to initialize default tenant", exception);

			throw exception;
		}

		log.info("Default tenant initialization completed");
	}

	private final TenantLocalService _tenantLocalService;

}