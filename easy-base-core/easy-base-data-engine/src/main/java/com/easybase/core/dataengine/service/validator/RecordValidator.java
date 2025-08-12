package com.easybase.core.dataengine.service.validator;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RecordValidator {

    public void validate(UUID tenantId, String table, Map<String, Object> data) {
        // TODO: Fetch attribute metadata
        // TODO: Ensure required fields exist
        // TODO: Type check values
        // For now, placeholder validation:
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }
    }
}
