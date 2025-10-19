/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Utility class for common String operations.
 *  Provides null-safe String manipulation methods.
 *
 * @author Akhash R
 */
public class ListUtil {

	public static <E> List<E> fromArray(E... array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		return new ArrayList<>(Arrays.asList(array));
	}

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

}