/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.impl;

import com.easybase.context.api.domain.PermissionContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Implementation of PermissionContext using immutable data structure.
 * Follows the same builder pattern as ServiceContextImpl for consistency.
 *
 * @author Akhash R
 */
@Getter
public class PermissionContextImpl implements PermissionContext {

	/**
	 * Creates a builder for PermissionContextImpl.
	 *
	 * @return a new PermissionContextBuilder
	 */
	public static PermissionContextBuilder builder() {
		return new PermissionContextBuilder();
	}

	@Override
	public Set<String> permissions() {
		return _permissions;
	}

	@Override
	public List<String> roles() {
		return _roles;
	}

	@Override
	public UUID tenantId() {
		return _tenantId;
	}

	@Override
	public UUID userId() {
		return _userId;
	}

	/**
	 * Builder for creating PermissionContextImpl instances.
	 * Follows the same pattern as ServiceContextBuilder.
	 */
	public static class PermissionContextBuilder {

		/**
		 * Builds the PermissionContext.
		 *
		 * @return the created PermissionContext
		 */
		public PermissionContext build() {
			PermissionContextImpl permissionContextImpl =
				new PermissionContextImpl();

			permissionContextImpl._userId = _userId;
			permissionContextImpl._tenantId = _tenantId;
			permissionContextImpl._permissions = _permissions;
			permissionContextImpl._roles = _roles;

			return permissionContextImpl;
		}

		/**
		 * Sets the permissions.
		 *
		 * @param permissions the set of permission keys
		 * @return this builder for chaining
		 */
		public PermissionContextBuilder permissions(Set<String> permissions) {
			_permissions = permissions;

			return this;
		}

		/**
		 * Sets the roles.
		 *
		 * @param roles the list of role names
		 * @return this builder for chaining
		 */
		public PermissionContextBuilder roles(List<String> roles) {
			_roles = roles;

			return this;
		}

		/**
		 * Sets the tenant ID.
		 *
		 * @param tenantId the tenant ID
		 * @return this builder for chaining
		 */
		public PermissionContextBuilder tenantId(UUID tenantId) {
			_tenantId = tenantId;

			return this;
		}

		/**
		 * Sets the user ID.
		 *
		 * @param userId the user ID
		 * @return this builder for chaining
		 */
		public PermissionContextBuilder userId(UUID userId) {
			_userId = userId;

			return this;
		}

		private Set<String> _permissions;
		private List<String> _roles;
		private UUID _tenantId;
		private UUID _userId;

	}

	private Set<String> _permissions;
	private List<String> _roles;
	private UUID _tenantId;
	private UUID _userId;

}