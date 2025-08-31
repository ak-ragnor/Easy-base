/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DataRecordDto {

	@JsonAnySetter
	public void addData(String key, Object value) {
		if (key == null) {
			return;
		}

		data.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, Object> getData() {
		return data;
	}

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@NotNull(message = "Data is required")
	private Map<String, Object> data = new HashMap<>();

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;

}