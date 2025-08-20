package com.easybase.api.data.engine.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Collection name is required")
	@Size(min = 1, max = 63, message = "Collection name must be between 1 and 63 characters")
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "Collection name must start with a letter and contain only alphanumeric characters and underscores")
	private String name;

	private List<AttributeDto> attributes;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;
}
