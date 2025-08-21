package com.easybase.api.data.engine.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.easybase.core.data.engine.enums.AttributeType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for attribute information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Attribute name is required")
	private String name;

	@NotNull(message = "Attribute type is required") private AttributeType type;

	private boolean indexed;

}
