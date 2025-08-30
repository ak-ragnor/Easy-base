package com.easybase.api.data.engine.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataRecordDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@NotNull(message = "Data is required") private Map<String, Object> data = new HashMap<>();

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;

	@JsonAnyGetter
	public Map<String, Object> getData() {
		return data;
	}

	@JsonAnySetter
	public void addData(String key, Object value) {
		if (key == null) {
			return;
		}

		data.put(key, value);
	}
}
