package com.easybase.api.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Email is required")
	@Size(max = 255, message = "Email must not exceed 255 characters")
	@Email(message = "Email should be valid")
	private String email;

	@Size(max = 100, message = "First name must not exceed 100 characters")
	private String firstName;

	@Size(max = 100, message = "Last name must not exceed 100 characters")
	private String lastName;

	@Size(max = 100, message = "Display name must not exceed 100 characters")
	private String displayName;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID tenantId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;
}
