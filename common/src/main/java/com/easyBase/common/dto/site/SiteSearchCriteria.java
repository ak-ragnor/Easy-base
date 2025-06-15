package com.easyBase.common.dto.site;

import com.easyBase.common.enums.SiteStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Site Search Criteria DTO
 *
 * Data Transfer Object for site search and filtering operations.
 * Used with specifications for dynamic query building.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public class SiteSearchCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String searchTerm;
    private String code;
    private String name;
    private String description;
    private List<SiteStatus> statuses;
    private List<String> timeZones;
    private List<String> languageCodes;
    private Boolean accessible;
    private Boolean operational;
    private Long userId; // For filtering sites accessible by specific user
    private Boolean adminAccessOnly; // Show only sites where user has admin access
    private ZonedDateTime createdAfter;
    private ZonedDateTime createdBefore;
    private ZonedDateTime modifiedAfter;
    private ZonedDateTime modifiedBefore;
    private Integer minimumUsers;
    private Integer maximumUsers;

    // Constructors
    public SiteSearchCriteria() {}

    // Getters and Setters
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<SiteStatus> getStatuses() { return statuses; }
    public void setStatuses(List<SiteStatus> statuses) { this.statuses = statuses; }

    public List<String> getTimeZones() { return timeZones; }
    public void setTimeZones(List<String> timeZones) { this.timeZones = timeZones; }

    public List<String> getLanguageCodes() { return languageCodes; }
    public void setLanguageCodes(List<String> languageCodes) { this.languageCodes = languageCodes; }

    public Boolean getAccessible() { return accessible; }
    public void setAccessible(Boolean accessible) { this.accessible = accessible; }

    public Boolean getOperational() { return operational; }
    public void setOperational(Boolean operational) { this.operational = operational; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Boolean getAdminAccessOnly() { return adminAccessOnly; }
    public void setAdminAccessOnly(Boolean adminAccessOnly) { this.adminAccessOnly = adminAccessOnly; }

    public ZonedDateTime getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(ZonedDateTime createdAfter) { this.createdAfter = createdAfter; }

    public ZonedDateTime getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(ZonedDateTime createdBefore) { this.createdBefore = createdBefore; }

    public ZonedDateTime getModifiedAfter() { return modifiedAfter; }
    public void setModifiedAfter(ZonedDateTime modifiedAfter) { this.modifiedAfter = modifiedAfter; }

    public ZonedDateTime getModifiedBefore() { return modifiedBefore; }
    public void setModifiedBefore(ZonedDateTime modifiedBefore) { this.modifiedBefore = modifiedBefore; }

    public Integer getMinimumUsers() { return minimumUsers; }
    public void setMinimumUsers(Integer minimumUsers) { this.minimumUsers = minimumUsers; }

    public Integer getMaximumUsers() { return maximumUsers; }
    public void setMaximumUsers(Integer maximumUsers) { this.maximumUsers = maximumUsers; }

    /**
     * Check if any search criteria are specified
     */
    public boolean hasSearchCriteria() {
        return (searchTerm != null && !searchTerm.trim().isEmpty()) ||
                (code != null && !code.trim().isEmpty()) ||
                (name != null && !name.trim().isEmpty()) ||
                (description != null && !description.trim().isEmpty()) ||
                (statuses != null && !statuses.isEmpty()) ||
                (timeZones != null && !timeZones.isEmpty()) ||
                (languageCodes != null && !languageCodes.isEmpty()) ||
                accessible != null ||
                operational != null ||
                userId != null ||
                adminAccessOnly != null ||
                createdAfter != null ||
                createdBefore != null ||
                modifiedAfter != null ||
                modifiedBefore != null ||
                minimumUsers != null ||
                maximumUsers != null;
    }

    @Override
    public String toString() {
        return "SiteSearchCriteria{" +
                "searchTerm='" + searchTerm + '\'' +
                ", statuses=" + statuses +
                ", userId=" + userId +
                ", adminAccessOnly=" + adminAccessOnly +
                ", accessible=" + accessible +
                ", operational=" + operational +
                '}';
    }
}

