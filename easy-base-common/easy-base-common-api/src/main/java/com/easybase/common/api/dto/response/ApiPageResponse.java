package com.easybase.common.api.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

/**
 * API response wrapper for paginated data.
 */
@Getter
public class ApiPageResponse<T> extends ApiResponse<List<T>> {

	private final int page;

	private final int size;

	private final long totalElements;

	private final int totalPages;

	private final boolean first;

	private final boolean last;

	private ApiPageResponse(List<T> data, Page<?> page) {
		super(true, null, data, null, 200, null);
		this.page = page.getNumber();
		this.size = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.first = page.isFirst();
		this.last = page.isLast();
	}

	public static <T> ApiPageResponse<T> success(Page<T> page) {
		return new ApiPageResponse<>(page.getContent(), page);
	}

	public static <T> ApiPageResponse<T> success(List<T> content,
			Page<?> page) {
		return new ApiPageResponse<>(content, page);
	}
}
