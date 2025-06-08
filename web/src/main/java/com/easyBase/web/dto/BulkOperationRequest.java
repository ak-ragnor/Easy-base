package com.easyBase.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Bulk operation request for user management
 *
 * Handles requests for bulk operations like:
 * - Status updates
 * - Role changes
 * - Bulk deletions
 * - Mass activations/deactivations
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public class BulkOperationRequest {

    @NotEmpty(message = "User IDs list cannot be empty")
    @Size(max = 1000, message = "Cannot process more than 1000 users at once")
    private List<@NotNull @Positive Long> userIds;

    @NotNull(message = "New status is required")
    private String newStatus;

    private String reason;

    // Constructors
    public BulkOperationRequest() {}

    public BulkOperationRequest(List<Long> userIds, String newStatus) {
        this.userIds = userIds;
        this.newStatus = newStatus;
    }

    // Getters and Setters
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return String.format("BulkOperationRequest{userIds=%s, newStatus='%s', reason='%s'}",
                userIds != null ? userIds.size() + " users" : "null", newStatus, reason);
    }
}
