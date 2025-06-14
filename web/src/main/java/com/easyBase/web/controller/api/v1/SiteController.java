package com.easyBase.web.controller.api.v1;

import com.easyBase.common.dto.site.*;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import com.easyBase.service.business.SiteService;
import com.easyBase.web.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Site REST Controller
 *
 * Provides RESTful API endpoints for site management operations.
 * Includes comprehensive CRUD operations, search capabilities,
 * and user-site relationship management.
 *
 * Security:
 * - Most operations require ADMIN or SUPER_ADMIN role
 * - Read operations may be accessible to authenticated users
 * - Site-specific access control based on user-site relationships
 *
 * API Versioning: v1
 * Base Path: /api/v1/sites
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/sites")
@Validated
@Tag(name = "Site Management", description = "Site management operations")
public class SiteController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

    @Autowired
    private SiteService siteService;

    // ===== CORE CRUD OPERATIONS =====

    @PostMapping
    @Operation(summary = "Create a new site", description = "Creates a new site in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Site created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Site code already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<SiteDTO> createSite(
            @Valid @RequestBody CreateSiteRequest request) {

        logger.info("Creating new site with code: {}", request.getCode());

        SiteDTO createdSite = siteService.createSite(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdSite);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get site by ID", description = "Retrieves a site by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site found"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<SiteDTO> getSiteById(
            @Parameter(description = "Site ID") @PathVariable Long id) {

        logger.debug("Getting site by ID: {}", id);

        SiteDTO site = siteService.findById(id);

        return ResponseEntity.ok(site);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get site by code", description = "Retrieves a site by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site found"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<SiteDTO> getSiteByCode(
            @Parameter(description = "Site code") @PathVariable String code) {

        logger.debug("Getting site by code: {}", code);

        SiteDTO site = siteService.findByCode(code);

        return ResponseEntity.ok(site);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update site", description = "Updates an existing site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site updated successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<SiteDTO> updateSite(
            @Parameter(description = "Site ID") @PathVariable Long id,
            @Valid @RequestBody UpdateSiteRequest request) {

        logger.info("Updating site with ID: {}", id);

        SiteDTO updatedSite = siteService.updateSite(id, request);

        return ResponseEntity.ok(updatedSite);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete site", description = "Deletes a site (SUPER_ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Site deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "400", description = "Site has active users"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> deleteSite(
            @Parameter(description = "Site ID") @PathVariable Long id,
            @Parameter(description = "Force delete even if site has active users")
            @RequestParam(defaultValue = "false") boolean force) {

        logger.warn("Deleting site with ID: {}, force: {}", id, force);

        siteService.deleteSite(id, force);

        return ResponseEntity.noContent().build();
    }

    // ===== SEARCH AND LISTING OPERATIONS =====

    @GetMapping
    @Operation(summary = "Get all sites", description = "Retrieves all sites with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sites retrieved successfully")
    })
    public ResponseEntity<Page<SiteDTO>> getAllSites(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting all sites with pagination: {}", pageable);

        Page<SiteDTO> sites = siteService.findAllSites(pageable);

        return ResponseEntity.ok(sites);
    }

    @PostMapping("/search")
    @Operation(summary = "Search sites", description = "Search sites using various criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<SiteDTO>> searchSites(
            @Valid @RequestBody SiteSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Searching sites with criteria: {}", criteria);

        Page<SiteDTO> sites = siteService.searchSites(criteria, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get sites by status", description = "Retrieves sites filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sites retrieved successfully")
    })
    public ResponseEntity<Page<SiteDTO>> getSitesByStatus(
            @Parameter(description = "Site status") @PathVariable SiteStatus status,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting sites by status: {}", status);

        Page<SiteDTO> sites = siteService.findSitesByStatus(status, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active sites", description = "Retrieves all active sites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active sites retrieved successfully")
    })
    public ResponseEntity<List<SiteDTO>> getActiveSites() {

        logger.debug("Getting all active sites");

        List<SiteDTO> sites = siteService.findActiveSites();

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/accessible")
    @Operation(summary = "Get accessible sites", description = "Retrieves all accessible sites (active or maintenance)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessible sites retrieved successfully")
    })
    public ResponseEntity<List<SiteDTO>> getAccessibleSites() {

        logger.debug("Getting all accessible sites");

        List<SiteDTO> sites = siteService.findAccessibleSites();

        return ResponseEntity.ok(sites);
    }

    // ===== USER-SITE RELATIONSHIP OPERATIONS =====

    @PostMapping("/{siteId}/users/{userId}")
    @Operation(summary = "Add user to site", description = "Grants a user access to a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added to site successfully"),
            @ApiResponse(responseCode = "404", description = "Site or user not found"),
            @ApiResponse(responseCode = "409", description = "User already has access to site"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<UserSiteDTO> addUserToSite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Site-specific role") @RequestParam(required = false) UserRole siteRole,
            @Parameter(description = "Notes about the access grant") @RequestParam(required = false) String notes) {

        logger.info("Adding user {} to site {} with role {}", userId, siteId, siteRole);

        // Get current user ID from security context (implementation would depend on security setup)
        Long grantedByUserId = getCurrentUserId();

        UserSiteDTO userSite = siteService.addUserToSite(siteId, userId, siteRole, grantedByUserId, notes);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userSite);
    }

    @DeleteMapping("/{siteId}/users/{userId}")
    @Operation(summary = "Remove user from site", description = "Revokes a user's access to a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User removed from site successfully"),
            @ApiResponse(responseCode = "404", description = "Site, user, or relationship not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> removeUserFromSite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "User ID") @PathVariable Long userId) {

        logger.info("Removing user {} from site {}", userId, siteId);

        // Get current user ID from security context
        Long revokedByUserId = getCurrentUserId();

        siteService.removeUserFromSite(siteId, userId, revokedByUserId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{siteId}/users/{userId}/role")
    @Operation(summary = "Update user role in site", description = "Updates a user's role within a specific site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Site, user, or relationship not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<UserSiteDTO> updateUserSiteRole(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "New site-specific role") @RequestParam @NotNull UserRole newSiteRole) {

        logger.info("Updating user {} role in site {} to {}", userId, siteId, newSiteRole);

        // Get current user ID from security context
        Long modifiedByUserId = getCurrentUserId();

        UserSiteDTO userSite = siteService.updateUserSiteRole(siteId, userId, newSiteRole, modifiedByUserId);

        return ResponseEntity.ok(userSite);
    }

    @PostMapping("/{siteId}/users/{userId}/activate")
    @Operation(summary = "Activate user access", description = "Activates a user's access to a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User access activated successfully"),
            @ApiResponse(responseCode = "404", description = "Site, user, or relationship not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<UserSiteDTO> activateUserSiteAccess(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "User ID") @PathVariable Long userId) {

        logger.info("Activating user {} access to site {}", userId, siteId);

        // Get current user ID from security context
        Long activatedByUserId = getCurrentUserId();

        UserSiteDTO userSite = siteService.activateUserSiteAccess(siteId, userId, activatedByUserId);

        return ResponseEntity.ok(userSite);
    }

    @PostMapping("/{siteId}/users/{userId}/deactivate")
    @Operation(summary = "Deactivate user access", description = "Deactivates a user's access to a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User access deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Site, user, or relationship not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<UserSiteDTO> deactivateUserSiteAccess(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "User ID") @PathVariable Long userId) {

        logger.info("Deactivating user {} access to site {}", userId, siteId);

        // Get current user ID from security context
        Long deactivatedByUserId = getCurrentUserId();

        UserSiteDTO userSite = siteService.deactivateUserSiteAccess(siteId, userId, deactivatedByUserId);

        return ResponseEntity.ok(userSite);
    }

    @GetMapping("/{siteId}/users")
    @Operation(summary = "Get users in site", description = "Retrieves all users associated with a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<UserSiteDTO>> getUsersBySite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @PageableDefault(size = 20, sort = "user.name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting users for site: {}", siteId);

        Page<UserSiteDTO> userSites = siteService.findUsersBySite(siteId, pageable);

        return ResponseEntity.ok(userSites);
    }

    // ===== BULK OPERATIONS =====

    @PostMapping("/bulk/status")
    @Operation(summary = "Bulk update site status", description = "Updates status for multiple sites (SUPER_ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sites updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> bulkUpdateSiteStatus(
            @Parameter(description = "List of site IDs") @RequestParam List<Long> siteIds,
            @Parameter(description = "New status") @RequestParam SiteStatus newStatus) {

        logger.info("Bulk updating {} sites to status {}", siteIds.size(), newStatus);

        // Get current user ID from security context
        Long updatedByUserId = getCurrentUserId();

        int updatedCount = siteService.bulkUpdateSiteStatus(siteIds, newStatus, updatedByUserId);

        Map<String, Object> response = Map.of(
                "updatedCount", updatedCount,
                "requestedCount", siteIds.size(),
                "newStatus", newStatus
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{siteId}/users/bulk")
    @Operation(summary = "Bulk add users to site", description = "Adds multiple users to a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users added successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> bulkAddUsersToSite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "List of user IDs") @RequestParam List<Long> userIds,
            @Parameter(description = "Site-specific role for all users") @RequestParam(required = false) UserRole siteRole) {

        logger.info("Bulk adding {} users to site {} with role {}", userIds.size(), siteId, siteRole);

        // Get current user ID from security context
        Long grantedByUserId = getCurrentUserId();

        List<UserSiteDTO> userSites = siteService.bulkAddUsersToSite(siteId, userIds, siteRole, grantedByUserId);

        Map<String, Object> response = Map.of(
                "addedCount", userSites.size(),
                "requestedCount", userIds.size(),
                "userSites", userSites
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{siteId}/users/bulk")
    @Operation(summary = "Bulk remove users from site", description = "Removes multiple users from a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users removed successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> bulkRemoveUsersFromSite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "List of user IDs") @RequestParam List<Long> userIds) {

        logger.info("Bulk removing {} users from site {}", userIds.size(), siteId);

        // Get current user ID from security context
        Long revokedByUserId = getCurrentUserId();

        int removedCount = siteService.bulkRemoveUsersFromSite(siteId, userIds, revokedByUserId);

        Map<String, Object> response = Map.of(
                "removedCount", removedCount,
                "requestedCount", userIds.size()
        );

        return ResponseEntity.ok(response);
    }

    // ===== VALIDATION AND UTILITY OPERATIONS =====

    @GetMapping("/validate/code/{code}")
    @Operation(summary = "Check site code availability", description = "Checks if a site code is available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code availability checked")
    })
    public ResponseEntity<Map<String, Object>> checkSiteCodeAvailability(
            @Parameter(description = "Site code to check") @PathVariable String code) {

        logger.debug("Checking availability of site code: {}", code);

        boolean available = siteService.isSiteCodeAvailable(code);
        boolean validFormat = true;

        try {
            siteService.validateSiteCode(code);
        } catch (Exception e) {
            validFormat = false;
        }

        Map<String, Object> response = Map.of(
                "code", code,
                "available", available,
                "validFormat", validFormat
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{siteId}/can-delete")
    @Operation(summary = "Check if site can be deleted", description = "Checks if a site can be safely deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete eligibility checked"),
            @ApiResponse(responseCode = "404", description = "Site not found")
    })
    public ResponseEntity<Map<String, Object>> checkSiteCanBeDeleted(
            @Parameter(description = "Site ID") @PathVariable Long siteId) {

        logger.debug("Checking if site {} can be deleted", siteId);

        boolean canDelete = siteService.canDeleteSite(siteId);
        long activeUsers = siteService.countActiveUsersInSite(siteId);
        long totalUsers = siteService.countTotalUsersInSite(siteId);

        Map<String, Object> response = Map.of(
                "siteId", siteId,
                "canDelete", canDelete,
                "activeUsers", activeUsers,
                "totalUsers", totalUsers
        );

        return ResponseEntity.ok(response);
    }

    // ===== STATISTICS AND ANALYTICS =====

    @GetMapping("/{siteId}/statistics")
    @Operation(summary = "Get site statistics", description = "Retrieves detailed statistics for a site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Site not found")
    })
    public ResponseEntity<Map<String, Object>> getSiteStatistics(
            @Parameter(description = "Site ID") @PathVariable Long siteId) {

        logger.debug("Getting statistics for site: {}", siteId);

        Map<String, Object> statistics = siteService.getSiteStatistics(siteId);

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/system")
    @Operation(summary = "Get system site statistics", description = "Retrieves system-wide site statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "System statistics retrieved successfully")
    })
    public ResponseEntity<Map<String, Object>> getSystemSiteStatistics() {

        logger.debug("Getting system-wide site statistics");

        Map<String, Object> statistics = siteService.getSystemSiteStatistics();

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/status-distribution")
    @Operation(summary = "Get site status distribution", description = "Retrieves distribution of sites by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status distribution retrieved successfully")
    })
    public ResponseEntity<Map<SiteStatus, Long>> getSiteStatusDistribution() {

        logger.debug("Getting site status distribution");

        Map<SiteStatus, Long> distribution = siteService.getSiteStatusDistribution();

        return ResponseEntity.ok(distribution);
    }

    // ===== HELPER METHODS =====

    /**
     * Get current user ID from security context
     * This would be implemented based on your security setup
     */
    private Long getCurrentUserId() {
        // Implementation would depend on your security context setup
        // For now, return a placeholder value
        return 1000L; // This would typically come from SecurityContextHolder
    }
}