package com.easyBase.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends ApiResponse<PageResponse.PageData<T>> {

    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setMessage("Success");

        PageData<T> pageData = new PageData<>();
        pageData.setContent(page.getContent());
        pageData.setTotalElements(page.getTotalElements());
        pageData.setTotalPages(page.getTotalPages());
        pageData.setSize(page.getSize());
        pageData.setNumber(page.getNumber());
        pageData.setFirst(page.isFirst());
        pageData.setLast(page.isLast());

        response.setData(pageData);
        return response;
    }

    @Data
    public static class PageData<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
        private int size;
        private int number;
        private boolean first;
        private boolean last;
    }
}