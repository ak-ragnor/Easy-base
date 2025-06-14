package com.easyBase.common.dto.site;

import com.easyBase.common.enums.SiteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

/**
 * Update Site Request DTO
 *
 * Data Transfer Object for updating existing sites.
 * Contains validation constraints to ensure data integrity.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public class UpdateSiteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Site name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Site name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Site status is required")
    private SiteStatus status;

    @NotBlank(message = "Timezone is required")
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timeZone;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 10, message = "Language code must be between 2 and 10 characters")
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "Language code must follow ISO format (e.g., 'en', 'en-US')")
    private String languageCode;

    // Constructors
    public UpdateSiteRequest() {}

    public UpdateSiteRequest(String name, SiteStatus status, String timeZone, String languageCode) {
        this.name = name;
        this.status = status;
        this.timeZone = timeZone;
        this.languageCode = languageCode;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SiteStatus getStatus() { return status; }
    public void setStatus(SiteStatus status) { this.status = status; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    @Override
    public String toString() {
        return "UpdateSiteRequest{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", timeZone='" + timeZone + '\'' +
                ", languageCode='" + languageCode + '\'' +
                '}';
    }
}