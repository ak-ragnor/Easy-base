/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.util;

import com.easybase.infrastructure.search.QueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Akhash R
 */
public class PageUtil {

	public static <T> Page<T> from(QueryResult<T> result) {
		return new PageImpl<>(
				result.getContent(),
				PageRequest.of(
						result.getPage(),
						Math.max(result.getSize(), 1)
				),
				result.getTotalElements()
		);
	}

	public static String sortFrom(Pageable pageable) {
		Sort sort = pageable.getSort();

		if (sort.isUnsorted()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (Sort.Order order : sort) {
			if (sb.length() > 0) {
				sb.append(",");
			}

			if (order.isDescending()) {
				sb.append("-");
			}

			sb.append(order.getProperty());
		}

		return sb.toString();
	}

	private PageUtil() {
	}

}