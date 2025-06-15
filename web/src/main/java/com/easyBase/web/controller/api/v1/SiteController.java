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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Site REST Controller Implementation
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/v1/sites")
@Validated
@Tag(name = "Site Management", description = "Site management operations")
public class SiteController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

    @Autowired
    private SiteService siteService;

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

    @GetMapping
    @Operation(summary = "Get all sites", description = "Retrieves all sites with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sites retrieved successfully")
    })
    public ResponseEntity<Page<SiteDTO>> getAllSites(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        int validatedSize = Math.min(Math.max(size, 1), 100); // Between 1 and 100
        int validatedPage = Math.max(page, 0); // Non-negative

        Pageable pageable = PageRequest.of(validatedPage, validatedSize, Sort.by("name"));

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

        Long revokedByUserId = getCurrentUserId();

        siteService.removeUserFromSite(siteId, userId, revokedByUserId);

        return ResponseEntity.noContent().build();
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