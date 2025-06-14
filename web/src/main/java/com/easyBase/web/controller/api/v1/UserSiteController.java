package com.easyBase.web.controller.api.v1;

import com.easyBase.common.dto.site.SiteDTO;
import com.easyBase.common.dto.site.UserSiteDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User-Site REST Controller
 *
 * Provides RESTful API endpoints for user-centric site operations.
 * Focuses on operations where users interact with sites from their perspective,
 * such as viewing accessible sites, checking access permissions, etc.
 *
 * Security:
 * - Users can view their own site access
 * - Admin operations require appropriate roles
 * - Site-specific access control based on user-site relationships
 *
 * API Versioning: v1
 * Base Path: /api/v1/users/{userId}/sites
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/sites")
@Validated
@Tag(name = "User-Site Management", description = "User-site relationship operations")
public class UserSiteController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserSiteController.class);

    @Autowired
    private SiteService siteService;

    // ===== USER SITE ACCESS OPERATIONS =====

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user's sites", description = "Retrieves all sites accessible by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User sites retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<UserSiteDTO>> getUserSites(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting sites for user: {}", userId);

        // Check if current user can access this information
        validateUserAccess(userId);

        Page<UserSiteDTO> sites = siteService.findSitesByUser(userId, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user's admin sites", description = "Retrieves sites where user has admin access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin sites retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<SiteDTO>> getUserAdminSites(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting admin sites for user: {}", userId);

        // Check if current user can access this information
        validateUserAccess(userId);

        Page<SiteDTO> sites = siteService.findAdminSitesByUser(userId, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/relationships")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user-site relationships", description = "Retrieves detailed user-site relationships")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relationships retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<UserSiteDTO>> getUserSiteRelationships(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "site.name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.debug("Getting user-site relationships for user: {}", userId);

        // Check if current user can access this information
        validateUserAccess(userId);

        Page<UserSiteDTO> userSites = siteService.findSitesByUser(userId, pageable);

        return ResponseEntity.ok(userSites);
    }

    // ===== SITE ACCESS VALIDATION =====

    @GetMapping("/{siteId}/access")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Check site access", description = "Checks if user has access to a specific site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access check completed"),
            @ApiResponse(responseCode = "404", description = "User or site not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> checkSiteAccess(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Site ID") @PathVariable Long siteId) {

        logger.debug("Checking site access for user {} to site {}", userId, siteId);

        // Check if current user can access this information
        validateUserAccess(userId);

        boolean hasAccess = siteService.hasUserAccessToSite(userId, siteId);
        boolean hasAdminAccess = siteService.hasUserAdminAccessToSite(userId, siteId);
        UserRole roleInSite = siteService.getUserRoleInSite(userId, siteId);

        Map<String, Object> response = Map.of(
                "userId", userId,
                "siteId", siteId,
                "hasAccess", hasAccess,
                "hasAdminAccess", hasAdminAccess,
                "roleInSite", roleInSite != null ? roleInSite : "NONE"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{siteId}/role")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user role in site", description = "Gets the user's effective role within a specific site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User, site, or relationship not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> getUserRoleInSite(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Site ID") @PathVariable Long siteId) {

        logger.debug("Getting user {} role in site {}", userId, siteId);

        // Check if current user can access this information
        validateUserAccess(userId);

        UserRole roleInSite = siteService.getUserRoleInSite(userId, siteId);
        boolean hasAccess = roleInSite != null;

        Map<String, Object> response = Map.of(
                "userId", userId,
                "siteId", siteId,
                "hasAccess", hasAccess,
                "roleInSite", roleInSite != null ? roleInSite : "NONE"
        );

        return ResponseEntity.ok(response);
    }

    // ===== SITE ACCESS REQUESTS (Future Enhancement) =====

    @PostMapping("/{siteId}/request-access")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Request site access", description = "Requests access to a site (future enhancement)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Access request submitted"),
            @ApiResponse(responseCode = "404", description = "User or site not found"),
            @ApiResponse(responseCode = "409", description = "User already has access or pending request"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> requestSiteAccess(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Parameter(description = "Reason for access request") @RequestParam(required = false) String reason) {

        logger.info("User {} requesting access to site {}", userId, siteId);

        // Check if current user can make this request
        validateUserAccess(userId);

        // TODO: Implement access request workflow
        // For now, return a placeholder response

        Map<String, Object> response = Map.of(
                "userId", userId,
                "siteId", siteId,
                "status", "PENDING",
                "message", "Access request submitted and pending approval",
                "reason", reason != null ? reason : ""
        );

        return ResponseEntity.accepted().body(response);
    }

    // ===== USER STATISTICS =====

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user site statistics", description = "Retrieves statistics about user's site access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Map<String, Object>> getUserSiteStatistics(
            @Parameter(description = "User ID") @PathVariable Long userId) {

        logger.debug("Getting site statistics for user: {}", userId);

        // Check if current user can access this information
        validateUserAccess(userId);

        // Get user's site access statistics
        Page<UserSiteDTO> allSites = siteService.findSitesByUser(userId, Pageable.unpaged());
        Page<SiteDTO> adminSites = siteService.findAdminSitesByUser(userId, Pageable.unpaged());

        long totalAccessibleSites = allSites.getTotalElements();
        long adminAccessibleSites = adminSites.getTotalElements();
        long regularAccessSites = totalAccessibleSites - adminAccessibleSites;

        Map<String, Object> statistics = Map.of(
                "userId", userId,
                "totalAccessibleSites", totalAccessibleSites,
                "adminAccessibleSites", adminAccessibleSites,
                "regularAccessSites", regularAccessSites,
                "accessibilityRatio", totalAccessibleSites > 0 ?
                        (double) adminAccessibleSites / totalAccessibleSites : 0.0
        );

        return ResponseEntity.ok(statistics);
    }

    // ===== HELPER METHODS =====

    /**
     * Validate that the current user can access information for the specified user
     * Users can access their own information, admins can access any user's information
     */
    private void validateUserAccess(Long userId) {
        Long currentUserId = getCurrentUserId();

        // Users can access their own information
        if (currentUserId.equals(userId)) {
            return;
        }

        // Check if current user has admin privileges
        if (!hasAdminRole()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Insufficient permissions to access user information");
        }
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

    /**
     * Check if current user has admin role
     */
    private boolean hasAdminRole() {
        // Implementation would depend on your security context setup
        // For now, return true as placeholder
        return true; // This would typically check SecurityContextHolder for roles
    }
}

// ===== ADDITIONAL CONTROLLER FOR CURRENT USER OPERATIONS =====

/**
 * Current User Sites Controller
 *
 * Simplified controller for operations on the current authenticated user's sites.
 * No need to specify userId in path since it's derived from security context.
 */
@RestController
@RequestMapping("/api/v1/my/sites")
@Validated
@Tag(name = "My Sites", description = "Current user's site operations")
class MySitesController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MySitesController.class);

    @Autowired
    private SiteService siteService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get my sites", description = "Retrieves sites accessible by the current user")
    public ResponseEntity<Page<UserSiteDTO>> getMySites(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Long currentUserId = getCurrentUserId();
        logger.debug("Getting sites for current user: {}", currentUserId);

        Page<UserSiteDTO> sites = siteService.findSitesByUser(currentUserId, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get my admin sites", description = "Retrieves sites where current user has admin access")
    public ResponseEntity<Page<SiteDTO>> getMyAdminSites(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Long currentUserId = getCurrentUserId();
        logger.debug("Getting admin sites for current user: {}", currentUserId);

        Page<SiteDTO> sites = siteService.findAdminSitesByUser(currentUserId, pageable);

        return ResponseEntity.ok(sites);
    }

    @GetMapping("/{siteId}/access")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Check my site access", description = "Checks current user's access to a specific site")
    public ResponseEntity<Map<String, Object>> checkMySiteAccess(
            @Parameter(description = "Site ID") @PathVariable Long siteId) {

        Long currentUserId = getCurrentUserId();
        logger.debug("Checking site access for current user {} to site {}", currentUserId, siteId);

        boolean hasAccess = siteService.hasUserAccessToSite(currentUserId, siteId);
        boolean hasAdminAccess = siteService.hasUserAdminAccessToSite(currentUserId, siteId);
        UserRole roleInSite = siteService.getUserRoleInSite(currentUserId, siteId);

        Map<String, Object> response = Map.of(
                "siteId", siteId,
                "hasAccess", hasAccess,
                "hasAdminAccess", hasAdminAccess,
                "roleInSite", roleInSite != null ? roleInSite : "NONE"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get my site statistics", description = "Retrieves statistics about current user's site access")
    public ResponseEntity<Map<String, Object>> getMySiteStatistics() {

        Long currentUserId = getCurrentUserId();
        logger.debug("Getting site statistics for current user: {}", currentUserId);

        Page<UserSiteDTO> allSites = siteService.findSitesByUser(currentUserId, Pageable.unpaged());
        Page<SiteDTO> adminSites = siteService.findAdminSitesByUser(currentUserId, Pageable.unpaged());

        long totalAccessibleSites = allSites.getTotalElements();
        long adminAccessibleSites = adminSites.getTotalElements();
        long regularAccessSites = totalAccessibleSites - adminAccessibleSites;

        Map<String, Object> statistics = Map.of(
                "totalAccessibleSites", totalAccessibleSites,
                "adminAccessibleSites", adminAccessibleSites,
                "regularAccessSites", regularAccessSites,
                "accessibilityRatio", totalAccessibleSites > 0 ?
                        (double) adminAccessibleSites / totalAccessibleSites : 0.0
        );

        return ResponseEntity.ok(statistics);
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        // Implementation would depend on your security context setup
        return 1000L; // Placeholder
    }
}