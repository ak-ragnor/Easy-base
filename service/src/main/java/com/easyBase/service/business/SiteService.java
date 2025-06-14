package com.easyBase.service.business;

import com.easyBase.common.dto.site.*;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Site Service Interface
 *
 * Provides comprehensive site management operations for the multi-tenant system.
 * Includes site CRUD operations, user-site relationship management,
 * and advanced search capabilities.
 *
 * Key Features:
 * - Full CRUD operations for sites
 * - User-site relationship management
 * - Advanced search and filtering
 * - Bulk operations for administrative tasks
 * - Site access control and validation
 * - Statistical and analytical methods
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public interface SiteService {

    // ===== CORE CRUD OPERATIONS =====

    /**
     * Create a new site
     *
     * @param request CreateSiteRequest with site details
     * @return Created SiteDTO
     * @throws BusinessException if site code already exists or validation fails
     */
    SiteDTO createSite(@Valid @NotNull CreateSiteRequest request);

    /**
     * Find site by ID
     *
     * @param id Site ID
     * @return SiteDTO if found
     * @throws ResourceNotFoundException if site not found
     */
    SiteDTO findById(@NotNull Long id);

    /**
     * Find site by code
     *
     * @param code Site code
     * @return SiteDTO if found
     * @throws ResourceNotFoundException if site not found
     */
    SiteDTO findByCode(@NotNull String code);

    /**
     * Update an existing site
     *
     * @param id      Site ID to update
     * @param request UpdateSiteRequest with new values
     * @return Updated SiteDTO
     * @throws ResourceNotFoundException if site not found
     * @throws BusinessException if validation fails
     */
    SiteDTO updateSite(@NotNull Long id, @Valid @NotNull UpdateSiteRequest request);

    /**
     * Delete a site
     * Note: This will also remove all user-site relationships
     *
     * @param id Site ID to delete
     * @throws ResourceNotFoundException if site not found
     * @throws BusinessException if site has active users and force is not enabled
     */
    void deleteSite(@NotNull Long id);

    /**
     * Delete a site with force option
     *
     * @param id    Site ID to delete
     * @param force If true, deletes even if site has active users
     * @throws ResourceNotFoundException if site not found
     * @throws BusinessException if site has active users and force is false
     */
    void deleteSite(@NotNull Long id, boolean force);

    // ===== SEARCH AND LISTING OPERATIONS =====

    /**
     * Find all sites with pagination
     *
     * @param pageable Pagination parameters
     * @return Page of SiteDTOs
     */
    Page<SiteDTO> findAllSites(Pageable pageable);

    /**
     * Search sites using criteria with pagination
     *
     * @param criteria Search criteria
     * @param pageable Pagination parameters
     * @return Page of SiteDTOs matching criteria
     */
    Page<SiteDTO> searchSites(@NotNull SiteSearchCriteria criteria, Pageable pageable);

    /**
     * Find sites by status
     *
     * @param status   Site status to filter by
     * @param pageable Pagination parameters
     * @return Page of SiteDTOs with specified status
     */
    Page<SiteDTO> findSitesByStatus(@NotNull SiteStatus status, Pageable pageable);

    /**
     * Find all active sites
     *
     * @return List of active SiteDTOs
     */
    List<SiteDTO> findActiveSites();

    /**
     * Find all accessible sites (active or maintenance)
     *
     * @return List of accessible SiteDTOs
     */
    List<SiteDTO> findAccessibleSites();

    /**
     * Find sites where user has admin access
     *
     * @param userId   User ID
     * @param pageable Pagination parameters
     * @return Page of SiteDTOs where user has admin access
     */
    Page<SiteDTO> findAdminSitesByUser(@NotNull Long userId, Pageable pageable);

    // ===== USER-SITE RELATIONSHIP MANAGEMENT =====

    /**
     * Add a user to a site
     *
     * @param siteId          Site ID
     * @param userId          User ID to add
     * @param siteRole        Site-specific role (optional)
     * @param grantedByUserId ID of user granting access
     * @param notes           Additional notes (optional)
     * @return Created UserSiteDTO
     * @throws ResourceNotFoundException if site or user not found
     * @throws BusinessException if user already has access or validation fails
     */
    UserSiteDTO addUserToSite(@NotNull Long siteId, @NotNull Long userId,
                              UserRole siteRole, @NotNull Long grantedByUserId, String notes);

    /**
     * Remove a user from a site
     *
     * @param siteId         Site ID
     * @param userId         User ID to remove
     * @param revokedByUserId ID of user revoking access
     * @throws ResourceNotFoundException if site, user, or relationship not found
     * @throws BusinessException if validation fails
     */
    void removeUserFromSite(@NotNull Long siteId, @NotNull Long userId, @NotNull Long revokedByUserId);

    /**
     * Update user's role in a site
     *
     * @param siteId         Site ID
     * @param userId         User ID
     * @param newSiteRole    New site-specific role
     * @param modifiedByUserId ID of user making the change
     * @return Updated UserSiteDTO
     * @throws ResourceNotFoundException if site, user, or relationship not found
     * @throws BusinessException if validation fails
     */
    UserSiteDTO updateUserSiteRole(@NotNull Long siteId, @NotNull Long userId,
                                   UserRole newSiteRole, @NotNull Long modifiedByUserId);

    /**
     * Activate user access to a site
     *
     * @param siteId         Site ID
     * @param userId         User ID
     * @param activatedByUserId ID of user activating access
     * @return Updated UserSiteDTO
     * @throws ResourceNotFoundException if site, user, or relationship not found
     */
    UserSiteDTO activateUserSiteAccess(@NotNull Long siteId, @NotNull Long userId, @NotNull Long activatedByUserId);

    /**
     * Deactivate user access to a site
     *
     * @param siteId           Site ID
     * @param userId           User ID
     * @param deactivatedByUserId ID of user deactivating access
     * @return Updated UserSiteDTO
     * @throws ResourceNotFoundException if site, user, or relationship not found
     */
    UserSiteDTO deactivateUserSiteAccess(@NotNull Long siteId, @NotNull Long userId, @NotNull Long deactivatedByUserId);

    /**
     * Find users in a site
     *
     * @param siteId   Site ID
     * @param pageable Pagination parameters
     * @return Page of UserSiteDTOs for the site
     */
    Page<UserSiteDTO> findUsersBySite(@NotNull Long siteId, Pageable pageable);

    /**
     * Find sites for a user
     *
     * @param userId   User ID
     * @param pageable Pagination parameters
     * @return Page of UserSiteDTOs for the user
     */
    Page<UserSiteDTO> findSitesByUser(@NotNull Long userId, Pageable pageable);

    /**
     * Check if user has access to a site
     *
     * @param userId User ID
     * @param siteId Site ID
     * @return true if user has active access to the site
     */
    boolean hasUserAccessToSite(@NotNull Long userId, @NotNull Long siteId);

    /**
     * Check if user has admin access to a site
     *
     * @param userId User ID
     * @param siteId Site ID
     * @return true if user has admin access to the site
     */
    boolean hasUserAdminAccessToSite(@NotNull Long userId, @NotNull Long siteId);

    /**
     * Get user's effective role in a site
     *
     * @param userId User ID
     * @param siteId Site ID
     * @return UserRole or null if user has no access
     */
    UserRole getUserRoleInSite(@NotNull Long userId, @NotNull Long siteId);

    // ===== BULK OPERATIONS =====

    /**
     * Bulk update site status
     *
     * @param siteIds   List of site IDs to update
     * @param newStatus New status to set
     * @param updatedByUserId ID of user performing the update
     * @return Number of sites updated
     */
    int bulkUpdateSiteStatus(@NotNull List<Long> siteIds, @NotNull SiteStatus newStatus, @NotNull Long updatedByUserId);

    /**
     * Bulk add users to a site
     *
     * @param siteId          Site ID
     * @param userIds         List of user IDs to add
     * @param siteRole        Site-specific role for all users
     * @param grantedByUserId ID of user granting access
     * @return List of created UserSiteDTOs
     */
    List<UserSiteDTO> bulkAddUsersToSite(@NotNull Long siteId, @NotNull List<Long> userIds,
                                         UserRole siteRole, @NotNull Long grantedByUserId);

    /**
     * Bulk remove users from a site
     *
     * @param siteId         Site ID
     * @param userIds        List of user IDs to remove
     * @param revokedByUserId ID of user revoking access
     * @return Number of users removed
     */
    int bulkRemoveUsersFromSite(@NotNull Long siteId, @NotNull List<Long> userIds, @NotNull Long revokedByUserId);

    // ===== VALIDATION AND BUSINESS RULES =====

    /**
     * Check if site code is available
     *
     * @param code Site code to check
     * @return true if code is available
     */
    boolean isSiteCodeAvailable(@NotNull String code);

    /**
     * Validate site code format
     *
     * @param code Site code to validate
     * @return true if code format is valid
     * @throws BusinessException if code format is invalid
     */
    boolean validateSiteCode(@NotNull String code);

    /**
     * Check if site can be deleted
     *
     * @param siteId Site ID to check
     * @return true if site can be safely deleted
     */
    boolean canDeleteSite(@NotNull Long siteId);

    /**
     * Validate site access for user
     *
     * @param userId User ID
     * @param siteId Site ID
     * @throws BusinessException if user cannot access the site
     */
    void validateSiteAccess(@NotNull Long userId, @NotNull Long siteId);

    // ===== STATISTICAL AND ANALYTICAL METHODS =====

    /**
     * Get site statistics
     *
     * @param siteId Site ID
     * @return Map containing various statistics about the site
     */
    Map<String, Object> getSiteStatistics(@NotNull Long siteId);

    /**
     * Get overall system site statistics
     *
     * @return Map containing system-wide site statistics
     */
    Map<String, Object> getSystemSiteStatistics();

    /**
     * Get site status distribution
     *
     * @return Map of site status to count
     */
    Map<SiteStatus, Long> getSiteStatusDistribution();

    /**
     * Get timezone distribution across sites
     *
     * @return Map of timezone to site count
     */
    Map<String, Long> getTimezoneDistribution();

    /**
     * Get language distribution across sites
     *
     * @return Map of language code to site count
     */
    Map<String, Long> getLanguageDistribution();

    /**
     * Count total sites
     *
     * @return Total number of sites
     */
    long countTotalSites();

    /**
     * Count sites by status
     *
     * @param status Site status
     * @return Number of sites with the specified status
     */
    long countSitesByStatus(@NotNull SiteStatus status);

    /**
     * Count active users in a site
     *
     * @param siteId Site ID
     * @return Number of active users in the site
     */
    long countActiveUsersInSite(@NotNull Long siteId);

    /**
     * Count total users in a site
     *
     * @param siteId Site ID
     * @return Total number of users in the site
     */
    long countTotalUsersInSite(@NotNull Long siteId);
}