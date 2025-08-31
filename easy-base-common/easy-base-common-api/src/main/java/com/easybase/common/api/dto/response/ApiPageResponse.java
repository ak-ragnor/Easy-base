/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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