package com.easybase.fs.dto;

import lombok.Builder;

/**
 * @author Saurasish
 * @Date 14-09-2025
 */
@Builder
public class ErrorResponse implements Response{
    private String reason;
}
