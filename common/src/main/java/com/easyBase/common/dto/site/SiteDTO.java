package com.easyBase.common.dto.site;

import com.easyBase.common.enums.SiteStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Site DTO
 *
 * Data Transfer Object representing a site for API responses.
 * Extends BaseDTO to include audit fields.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public class SiteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String description;
    private SiteStatus status;
    private String timeZone;
    private String languageCode;
    private boolean accessible;
    private boolean modifiable;
    private boolean operational;
    private Long activeUsersCount;
    private Long totalUsersCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("createdAt")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("lastModified")
    private ZonedDateTime lastModified;

    // Constructors
    public SiteDTO() {
        super();
    }

    public SiteDTO(Long id, String code, String name, SiteStatus status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.accessible = status != null && status.isAccessible();
        this.modifiable = status != null && status.isModifiable();
        this.operational = status != null && status.isOperational();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SiteStatus getStatus() { return status; }
    public void setStatus(SiteStatus status) {
        this.status = status;
        // Update computed flags when status changes
        this.accessible = status != null && status.isAccessible();
        this.modifiable = status != null && status.isModifiable();
        this.operational = status != null && status.isOperational();
    }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public boolean isAccessible() { return accessible; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }

    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }

    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational) { this.operational = operational; }

    public Long getActiveUsersCount() { return activeUsersCount; }
    public void setActiveUsersCount(Long activeUsersCount) { this.activeUsersCount = activeUsersCount; }

    public Long getTotalUsersCount() { return totalUsersCount; }
    public void setTotalUsersCount(Long totalUsersCount) { this.totalUsersCount = totalUsersCount; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getLastModified() { return lastModified; }
    public void setLastModified(ZonedDateTime lastModified) { this.lastModified = lastModified; }

    @Override
    public String toString() {
        return "SiteDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", timeZone='" + timeZone + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", accessible=" + accessible +
                ", activeUsersCount=" + activeUsersCount +
                '}';
    }
}
