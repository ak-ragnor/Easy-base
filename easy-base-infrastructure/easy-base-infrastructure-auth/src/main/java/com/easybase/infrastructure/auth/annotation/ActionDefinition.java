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
 * Class-level annotation to mark action definition classes.
 * Classes annotated with this define available actions for a specific resource type.
 *
 * @author Akhash R
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionDefinition {

	/**
	 * The resource type these actions apply to (e.g., "PERMISSION", "ROLE", "USER").
	 *
	 * @return the resource type
	 */
	String resourceType();

}