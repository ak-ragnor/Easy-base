package com.easyBase.common.dto.user;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;

import java.time.ZonedDateTime;

/**
 * User Search Criteria DTO - Hybrid Approach
 *
 * Simplified search criteria for the hybrid repository approach.
 * Contains only the most commonly used search parameters.
 *
 * For simple queries, use custom @Query methods in UserRepository.
 * For complex queries, use this DTO with UserSpecifications.
 *
 * @author Enterprise Team
 * @version 2.0 - Hybrid Approach
 * @since 1.0
 */
public class UserSearchCriteria {

    private String searchTerm;
    private UserRole role;
    private UserStatus status;
    private String timezone;
    private ZonedDateTime createdAfter;
    private ZonedDateTime createdBefore;

    // ===== CONSTRUCTORS =====

    public UserSearchCriteria() {}

    public UserSearchCriteria(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public UserSearchCriteria(UserRole role, UserStatus status) {
        this.role = role;
        this.status = status;
    }

    // ===== BUILDER PATTERN FOR EASY USAGE =====

    public static UserSearchCriteria builder() {
        return new UserSearchCriteria();
    }

    public UserSearchCriteria searchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public UserSearchCriteria role(UserRole role) {
        this.role = role;
        return this;
    }

    public UserSearchCriteria status(UserStatus status) {
        this.status = status;
        return this;
    }

    public UserSearchCriteria timezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public UserSearchCriteria createdAfter(ZonedDateTime createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    public UserSearchCriteria createdBefore(ZonedDateTime createdBefore) {
        this.createdBefore = createdBefore;
        return this;
    }

    // ===== GETTERS AND SETTERS =====

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public ZonedDateTime getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(ZonedDateTime createdAfter) { this.createdAfter = createdAfter; }

    public ZonedDateTime getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(ZonedDateTime createdBefore) { this.createdBefore = createdBefore; }

    // ===== UTILITY METHODS =====

    /**
     * Check if any search criteria is specified
     */
    public boolean hasAnyCriteria() {
        return searchTerm != null || role != null || status != null ||
                timezone != null || createdAfter != null || createdBefore != null;
    }

    /**
     * Reset all criteria
     */
    public void clear() {
        searchTerm = null;
        role = null;
        status = null;
        timezone = null;
        createdAfter = null;
        createdBefore = null;
    }

    @Override
    public String toString() {
        return "UserSearchCriteria{" +
                "searchTerm='" + searchTerm + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", timezone='" + timezone + '\'' +
                ", createdAfter=" + createdAfter +
                ", createdBefore=" + createdBefore +
                '}';
    }
}