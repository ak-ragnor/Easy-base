package com.easyBase.service.business;

import com.easyBase.common.dto.site.*;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import com.easyBase.service.exception.BusinessException;
import com.easyBase.service.exception.ResourceNotFoundException;
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
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public interface SiteService {

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
     * Delete a site with force option
     *
     * @param id    Site ID to delete
     * @param force If true, deletes even if site has active users
     * @throws ResourceNotFoundException if site not found
     * @throws BusinessException if site has active users and force is false
     */
    void deleteSite(@NotNull Long id, boolean force);

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
     * Find users in a site
     *
     * @param siteId   Site ID
     * @param pageable Pagination parameters
     * @return Page of UserSiteDTOs for the site
     */
    Page<UserSiteDTO> findUsersBySite(@NotNull Long siteId, Pageable pageable);

    /**
     * Check if user has access to a site
     *
     * @param userId User ID
     * @param siteId Site ID
     * @return true if user has active access to the site
     */
    boolean hasUserAccessToSite(@NotNull Long userId, @NotNull Long siteId);

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
     * Count active users in a site
     *
     * @param siteId Site ID
     * @return Number of active users in the site
     */
    long countActiveUsersInSite(@NotNull Long siteId);
}