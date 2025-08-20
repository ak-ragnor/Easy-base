package com.easybase.core.data.engine.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record DataRecord(UUID id, Map<String, Object> data,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
}
