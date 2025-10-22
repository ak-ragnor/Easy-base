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
 * Optional marker annotation for action constants.
 * Can be used to explicitly mark fields as actions, though not required
 * if the field is a public static final String in an @ActionDefinition class.
 *
 * @author Akhash R
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Action {

	/**
	 * Optional description of the action.
	 *
	 * @return action description
	 */
	String description() default "";

}