/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.dto.response;

import java.util.List;

import lombok.Getter;

import org.springframework.data.domain.Page;

/**
 * API response wrapper for paginated data.
 */
@Getter
public class ApiPageResponse<T> extends ApiResponse<List<T>> {

	public static <T> ApiPageResponse<T> success(
		List<T> content, Page<?> page) {

		return new ApiPageResponse<>(content, page);
	}

	public static <T> ApiPageResponse<T> success(Page<T> page) {
		return new ApiPageResponse<>(page.getContent(), page);
	}

	private ApiPageResponse(List<T> data, Page<?> page) {
		super(true, null, data, null, 200, null);

		this.page = page.getNumber();
		size = page.getSize();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		first = page.isFirst();
		last = page.isLast();
	}

	private final boolean first;
	private final boolean last;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;

}