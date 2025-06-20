package com.easyBase.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponse extends ApiResponse<Object> {
    private String error;
    private Integer status;
    private List<FieldError> fieldErrors;
    private Map<String, Object> details;
    private String stackTrace;

    @Data
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String message;

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }
}