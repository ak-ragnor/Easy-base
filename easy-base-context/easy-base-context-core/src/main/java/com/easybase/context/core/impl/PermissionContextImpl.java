/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.impl;

import com.easybase.context.api.domain.PermissionContext;

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
	public UUID userId() {
		return _userId;
	}

	@Override
	public UUID tenantId() {
		return _tenantId;
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
			PermissionContextImpl context = new PermissionContextImpl();
			context._userId = _userId;
			context._tenantId = _tenantId;
			context._permissions = _permissions;
			return context;
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
		 * Sets the permissions.
		 *
		 * @param permissions the set of permission keys
		 * @return this builder for chaining
		 */
		public PermissionContextBuilder permissions(Set<String> permissions) {
			_permissions = permissions;
			return this;
		}

		private UUID _userId;
		private UUID _tenantId;
		private Set<String> _permissions;

	}

	private UUID _userId;
	private UUID _tenantId;
	private Set<String> _permissions;

}