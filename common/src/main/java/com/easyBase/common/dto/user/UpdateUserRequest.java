package com.easyBase.common.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Update User Request DTO
 */
public class UpdateUserRequest {

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "ADMIN|USER|MANAGER", message = "Role must be ADMIN, USER, or MANAGER")
    private String role;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;

    @JsonProperty("timezone")
    private String timezone;

    // Constructors
    public UpdateUserRequest() {}

    public UpdateUserRequest(String name, String email, String role, String status, String timezone) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.timezone = timezone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}
