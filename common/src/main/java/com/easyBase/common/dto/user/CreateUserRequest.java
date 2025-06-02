package com.easyBase.common.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Create User Request DTO
 */
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "ADMIN|USER|MANAGER", message = "Role must be ADMIN, USER, or MANAGER")
    private String role;

    @JsonProperty("timezone")
    private String timezone;

    // Constructors
    public CreateUserRequest() {}

    public CreateUserRequest(String name, String email, String role, String timezone) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.timezone = timezone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}

