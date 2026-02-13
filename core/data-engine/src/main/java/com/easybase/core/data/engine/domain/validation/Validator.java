/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import java.util.Map;

/**
 * @author Akhash R
 */
public interface Validator {

	public void validate(
		String fieldName, Object value, Map<String, Object> config);

}