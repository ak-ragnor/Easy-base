/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.api.auth.dto.PermissionCheckResult;
import com.easybase.api.auth.service.PermissionChecker;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.auth.repository.PermissionRepository;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Implementation of PermissionChecker interface.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class PermissionCheckerImpl implements PermissionChecker {

	@Cacheable(
		key = "#root.target.serviceContext.userId()", value = "userPermissions"
	)
	@Override
	public Set<String> getUserPermissions() {
		if (!serviceContext.isAuthenticated()) {
			return Set.of();
		}

		return getUserPermissions(serviceContext.userId());
	}

	@Cacheable(
		key = "#tenantId + ':' + #root.target.serviceContext.userId()",
		value = "userTenantPermissions"
	)
	@Override
	public Set<String> getUserPermissions(UUID tenantId) {
		if (!serviceContext.isAuthenticated()) {
			return Set.of();
		}

		return getUserPermissions(serviceContext.userId(), tenantId);
	}

	@Cacheable(key = "#userId", value = "userPermissions")
	@Override
	public Set<String> getUserPermissions(UUID userId) {
		return permissionRepository.findActivePermissionsByUserId(
			userId
		).stream(
		).map(
			permission -> permission.getPermissionKey()
		).collect(
			Collectors.toSet()
		);
	}

	@Cacheable(
		key = "#tenantId + ':' + #userId", value = "userTenantPermissions"
	)
	@Override
	public Set<String> getUserPermissions(UUID userId, UUID tenantId) {
		return permissionRepository.findActivePermissionsByUserIdAndTenantId(
			userId, tenantId
		).stream(
		).map(
			permission -> permission.getPermissionKey()
		).collect(
			Collectors.toSet()
		);
	}

	@Override
	public boolean hasAllPermissions(String... permissionKeys) {
		if (!serviceContext.isAuthenticated()) {
			return false;
		}

		Set<String> userPermissions = getUserPermissions();

		return Arrays.stream(
			permissionKeys
		).allMatch(
			userPermissions::contains
		);
	}

	@Override
	public boolean hasAnyPermission(String... permissionKeys) {
		if (!serviceContext.isAuthenticated()) {
			return false;
		}

		Set<String> userPermissions = getUserPermissions();

		return Arrays.stream(
			permissionKeys
		).anyMatch(
			userPermissions::contains
		);
	}

	@Override
	public PermissionCheckResult hasPermission(String permissionKey) {
		if (!serviceContext.isAuthenticated()) {
			return PermissionCheckResult.denied(
				permissionKey, "User not authenticated");
		}

		UUID userId = serviceContext.userId();

		Set<String> userPermissions = getUserPermissions(userId);

		if (userPermissions.contains(permissionKey)) {
			return PermissionCheckResult.allowed(permissionKey);
		}

		return PermissionCheckResult.denied(
			permissionKey, "Permission not granted");
	}

	@Override
	public PermissionCheckResult hasPermission(
		UUID tenantId, String permissionKey) {

		if (!serviceContext.isAuthenticated()) {
			return PermissionCheckResult.denied(
				permissionKey, "User not authenticated");
		}

		UUID userId = serviceContext.userId();

		Set<String> userPermissions = getUserPermissions(userId, tenantId);

		if (userPermissions.contains(permissionKey)) {
			return PermissionCheckResult.allowed(permissionKey);
		}

		return PermissionCheckResult.denied(
			permissionKey, "Permission not granted for tenant");
	}

	@Override
	public PermissionCheckResult hasPermission(
		UUID userId, String permissionKey) {

		Set<String> userPermissions = getUserPermissions(userId);

		if (userPermissions.contains(permissionKey)) {
			return PermissionCheckResult.allowed(permissionKey);
		}

		return PermissionCheckResult.denied(
			permissionKey, "Permission not granted");
	}

	@Override
	public PermissionCheckResult hasPermission(
		UUID userId, UUID tenantId, String permissionKey) {

		Set<String> userPermissions = getUserPermissions(userId, tenantId);

		if (userPermissions.contains(permissionKey)) {
			return PermissionCheckResult.allowed(permissionKey);
		}

		return PermissionCheckResult.denied(
			permissionKey, "Permission not granted for tenant");
	}

	private final PermissionRepository permissionRepository;
	private final ServiceContext serviceContext;

}