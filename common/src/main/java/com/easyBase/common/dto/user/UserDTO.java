package com.easyBase.common.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

/**
 * User DTO with proper timezone support
 * This should be in the common module for reuse across services
 */
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("createdAt")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("lastModified")
    private ZonedDateTime lastModified;

    @JsonProperty("timezone")
    private String userTimezone;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String name, String email, String role, String status,
                   ZonedDateTime createdAt, ZonedDateTime lastModified, String userTimezone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.userTimezone = userTimezone;
    }

    // Builder pattern for easy construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String status;
        private ZonedDateTime createdAt;
        private ZonedDateTime lastModified;
        private String userTimezone;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastModified(ZonedDateTime lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder userTimezone(String userTimezone) {
            this.userTimezone = userTimezone;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, name, email, role, status, createdAt, lastModified, userTimezone);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getLastModified() { return lastModified; }
    public void setLastModified(ZonedDateTime lastModified) { this.lastModified = lastModified; }

    public String getUserTimezone() { return userTimezone; }
    public void setUserTimezone(String userTimezone) { this.userTimezone = userTimezone; }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", userTimezone='" + userTimezone + '\'' +
                '}';
    }
}