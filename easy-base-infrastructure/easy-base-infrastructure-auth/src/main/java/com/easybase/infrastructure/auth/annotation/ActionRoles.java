/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field-level annotation to specify which roles should have access to an action by default.
 * Used during action discovery to automatically assign permissions to roles.
 *
 * @author Akhash R
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ActionRoles {

	/**
	 * Array of role names that should have this permission by default.
	 *
	 * @return array of role names
	 */
	String[] value();

}