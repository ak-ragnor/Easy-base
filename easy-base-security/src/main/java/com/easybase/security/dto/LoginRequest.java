package com.easybase.security.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LoginRequest {

	@NotNull private UUID tenantId;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;
}
