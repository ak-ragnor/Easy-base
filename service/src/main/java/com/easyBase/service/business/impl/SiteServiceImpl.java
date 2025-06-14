package com.easyBase.service.business.impl;

import com.easyBase.common.dto.site.*;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.SiteRepository;
import com.easyBase.domain.repository.jpa.UserRepository;
import com.easyBase.domain.specification.SiteSpecifications;
import com.easyBase.service.business.SiteService;
import com.easyBase.service.exception.BusinessException;
import com.easyBase.service.exception.ResourceNotFoundException;
import com.easyBase.service.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Site Service Implementation
 *
 * Provides comprehensive site management operations with:
 * - Full CRUD operations with proper validation
 * - Advanced search and filtering capabilities
 * - User-site relationship management
 * - Business rule enforcement
 * - Statistical and analytical methods
 * - Audit trail integration
 * - Performance optimization
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
@Validated
public class SiteServiceImpl implements SiteService {

    private static final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteMapper siteMapper;

    // ===== CORE CRUD OPERATIONS =====

    @Override
    public SiteDTO createSite(@Valid @NotNull CreateSiteRequest request) {
        logger.debug("Creating new site with code: {}", request.getCode());

        // Validate site code availability
        if (!isSiteCodeAvailable(request.getCode())) {
            throw new BusinessException("Site code '" + request.getCode() + "' is already in use");
        }

        // Validate site code format
        validateSiteCode(request.getCode());

        // Convert request to entity
        Site site = siteMapper.toEntity(request);

        // Save the site
        Site savedSite = siteRepository.save(site);
        logger.info("Created site: {} with ID: {}", savedSite.getCode(), savedSite.getId());

        return siteMapper.toDTO(savedSite);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteDTO findById(@NotNull Long id) {
        logger.debug("Finding site by ID: {}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + id));

        return siteMapper.toDTO(site);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteDTO findByCode(@NotNull String code) {
        logger.debug("Finding site by code: {}", code);

        Site site = siteRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with code: " + code));

        return siteMapper.toDTO(site);
    }

    @Override
    public SiteDTO updateSite(@NotNull Long id, @Valid @NotNull UpdateSiteRequest request) {
        logger.debug("Updating site with ID: {}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + id));

        // Update the entity
        Site updatedSite = siteMapper.updateEntity(site, request);

        // Save the updated site
        Site savedSite = siteRepository.save(updatedSite);
        logger.info("Updated site: {} with ID: {}", savedSite.getCode(), savedSite.getId());

        return siteMapper.toDTO(savedSite);
    }

    @Override
    public void deleteSite(@NotNull Long id) {
        deleteSite(id, false);
    }

    @Override
    public void deleteSite(@NotNull Long id, boolean force) {
        logger.debug("Deleting site with ID: {}, force: {}", id, force);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + id));

        // Check if site can be deleted
        if (!force && !canDeleteSite(id)) {
            long activeUsers = countActiveUsersInSite(id);
            throw new BusinessException("Cannot delete site '" + site.getCode() +
                    "' as it has " + activeUsers + " active users. Use force=true to delete anyway.");
        }

        siteRepository.delete(site);
        logger.info("Deleted site: {} with ID: {}", site.getCode(), site.getId());
    }

    // ===== SEARCH AND LISTING OPERATIONS =====

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findAllSites(Pageable pageable) {
        logger.debug("Finding all sites with pagination: {}", pageable);

        Page<Site> sitePage = siteRepository.findAll(pageable);
        return sitePage.map(siteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> searchSites(@NotNull SiteSearchCriteria criteria, Pageable pageable) {
        logger.debug("Searching sites with criteria: {}", criteria);

        Specification<Site> specification = buildSearchSpecification(criteria);
        Page<Site> sitePage = siteRepository.findAll(specification, pageable);

        return sitePage.map(siteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findSitesByStatus(@NotNull SiteStatus status, Pageable pageable) {
        logger.debug("Finding sites by status: {}", status);

        Page<Site> sitePage = siteRepository.findByStatus(status, pageable);
        return sitePage.map(siteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> findActiveSites() {
        logger.debug("Finding all active sites");

        List<Site> sites = siteRepository.findActiveSites();
        return siteMapper.toDTOList(sites);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> findAccessibleSites() {
        logger.debug("Finding all accessible sites");

        List<Site> sites = siteRepository.findAccessibleSites();
        return siteMapper.toDTOList(sites);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findAdminSitesByUser(@NotNull Long userId, Pageable pageable) {
        logger.debug("Finding admin sites by user ID: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Site> sites = siteRepository.findAdminSitesByUserId(userId);

        // Convert to page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sites.size());
        List<Site> pageContent = sites.subList(start, end);

        Page<Site> sitePage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, sites.size());

        return sitePage.map(siteMapper::toDTO);
    }

    // ===== USER-SITE RELATIONSHIP MANAGEMENT =====

    @Override
    public UserSiteDTO addUserToSite(@NotNull Long siteId, @NotNull Long userId,
                                     UserRole siteRole, @NotNull Long grantedByUserId, String notes) {
        logger.debug("Adding user {} to site {} with role {}", userId, siteId, siteRole);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if user already has access to this site
        if (hasUserAccessToSite(userId, siteId)) {
            throw new BusinessException("User already has access to site: " + site.getCode());
        }

        // Create the user-site relationship
        UserSite userSite = siteMapper.createUserSite(user, site, siteRole, grantedByUserId);
        if (notes != null && !notes.trim().isEmpty()) {
            userSite.setNotes(notes.trim());
        }

        // Add to both sides of the relationship
        site.getUserSites().add(userSite);
        user.getUserSites().add(userSite);

        // Save the site (cascade will save the user-site relationship)
        siteRepository.save(site);

        logger.info("Added user {} to site {} with role {}", user.getEmail(), site.getCode(), siteRole);
        return siteMapper.toDTO(userSite);
    }

    @Override
    public void removeUserFromSite(@NotNull Long siteId, @NotNull Long userId, @NotNull Long revokedByUserId) {
        logger.debug("Removing user {} from site {}", userId, siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find the user-site relationship
        UserSite userSite = site.getUserSites().stream()
                .filter(us -> us.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User-site relationship not found for user " + userId + " and site " + siteId));

        // Deactivate the relationship instead of deleting (for audit trail)
        userSite.deactivate(revokedByUserId);

        siteRepository.save(site);
        logger.info("Removed user {} from site {}", user.getEmail(), site.getCode());
    }

    @Override
    public UserSiteDTO updateUserSiteRole(@NotNull Long siteId, @NotNull Long userId,
                                          UserRole newSiteRole, @NotNull Long modifiedByUserId) {
        logger.debug("Updating user {} role in site {} to {}", userId, siteId, newSiteRole);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        UserSite userSite = site.getUserSites().stream()
                .filter(us -> us.getUser().getId().equals(userId) && us.getIsActive())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active user-site relationship not found for user " + userId + " and site " + siteId));

        UserRole oldRole = userSite.getSiteRole();
        userSite.setSiteRole(newSiteRole);

        siteRepository.save(site);
        logger.info("Updated user {} role in site {} from {} to {}",
                userId, site.getCode(), oldRole, newSiteRole);

        return siteMapper.toDTO(userSite);
    }

    @Override
    public UserSiteDTO activateUserSiteAccess(@NotNull Long siteId, @NotNull Long userId, @NotNull Long activatedByUserId) {
        logger.debug("Activating user {} access to site {}", userId, siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        UserSite userSite = site.getUserSites().stream()
                .filter(us -> us.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User-site relationship not found for user " + userId + " and site " + siteId));

        userSite.activate();

        siteRepository.save(site);
        logger.info("Activated user {} access to site {}", userId, site.getCode());

        return siteMapper.toDTO(userSite);
    }

    @Override
    public UserSiteDTO deactivateUserSiteAccess(@NotNull Long siteId, @NotNull Long userId, @NotNull Long deactivatedByUserId) {
        logger.debug("Deactivating user {} access to site {}", userId, siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        UserSite userSite = site.getUserSites().stream()
                .filter(us -> us.getUser().getId().equals(userId) && us.getIsActive())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active user-site relationship not found for user " + userId + " and site " + siteId));

        userSite.deactivate(deactivatedByUserId);

        siteRepository.save(site);
        logger.info("Deactivated user {} access to site {}", userId, site.getCode());

        return siteMapper.toDTO(userSite);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSiteDTO> findUsersBySite(@NotNull Long siteId, Pageable pageable) {
        logger.debug("Finding users by site ID: {}", siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        // Get user sites as a list and create page manually
        List<UserSite> userSites = new ArrayList<>(site.getUserSites());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userSites.size());
        List<UserSite> pageContent = userSites.subList(start, end);

        Page<UserSite> userSitePage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, userSites.size());

        return userSitePage.map(siteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSiteDTO> findSitesByUser(@NotNull Long userId, Pageable pageable) {
        logger.debug("Finding sites by user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Get user sites as a list and create page manually
        List<UserSite> userSites = new ArrayList<>(user.getUserSites());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userSites.size());
        List<UserSite> pageContent = userSites.subList(start, end);

        Page<UserSite> userSitePage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, userSites.size());

        return userSitePage.map(siteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAccessToSite(@NotNull Long userId, @NotNull Long siteId) {
        logger.debug("Checking user {} access to site {}", userId, siteId);

        User user = userRepository.findById(userId).orElse(null);
        Site site = siteRepository.findById(siteId).orElse(null);

        if (user == null || site == null) {
            return false;
        }

        return user.hasAccessToSite(site);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAdminAccessToSite(@NotNull Long userId, @NotNull Long siteId) {
        logger.debug("Checking user {} admin access to site {}", userId, siteId);

        User user = userRepository.findById(userId).orElse(null);
        Site site = siteRepository.findById(siteId).orElse(null);

        if (user == null || site == null) {
            return false;
        }

        return user.hasAdminAccessToSite(site);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRole getUserRoleInSite(@NotNull Long userId, @NotNull Long siteId) {
        logger.debug("Getting user {} role in site {}", userId, siteId);

        User user = userRepository.findById(userId).orElse(null);
        Site site = siteRepository.findById(siteId).orElse(null);

        if (user == null || site == null) {
            return null;
        }

        return user.getRoleInSite(site);
    }

    // ===== BULK OPERATIONS =====

    @Override
    public int bulkUpdateSiteStatus(@NotNull List<Long> siteIds, @NotNull SiteStatus newStatus, @NotNull Long updatedByUserId) {
        logger.debug("Bulk updating {} sites to status {}", siteIds.size(), newStatus);

        if (siteIds.isEmpty()) {
            return 0;
        }

        int updatedCount = siteRepository.bulkUpdateStatus(siteIds, newStatus);
        logger.info("Bulk updated {} sites to status {} by user {}", updatedCount, newStatus, updatedByUserId);

        return updatedCount;
    }

    @Override
    public List<UserSiteDTO> bulkAddUsersToSite(@NotNull Long siteId, @NotNull List<Long> userIds,
                                                UserRole siteRole, @NotNull Long grantedByUserId) {
        logger.debug("Bulk adding {} users to site {} with role {}", userIds.size(), siteId, siteRole);

        if (userIds.isEmpty()) {
            return List.of();
        }

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        List<UserSiteDTO> result = new ArrayList<>();

        for (Long userId : userIds) {
            try {
                UserSiteDTO userSiteDTO = addUserToSite(siteId, userId, siteRole, grantedByUserId,
                        "Bulk assignment by user " + grantedByUserId);
                result.add(userSiteDTO);
            } catch (Exception e) {
                logger.warn("Failed to add user {} to site {}: {}", userId, siteId, e.getMessage());
                // Continue with other users
            }
        }

        logger.info("Bulk added {} users to site {}", result.size(), site.getCode());
        return result;
    }

    @Override
    public int bulkRemoveUsersFromSite(@NotNull Long siteId, @NotNull List<Long> userIds, @NotNull Long revokedByUserId) {
        logger.debug("Bulk removing {} users from site {}", userIds.size(), siteId);

        if (userIds.isEmpty()) {
            return 0;
        }

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        int removedCount = 0;

        for (Long userId : userIds) {
            try {
                removeUserFromSite(siteId, userId, revokedByUserId);
                removedCount++;
            } catch (Exception e) {
                logger.warn("Failed to remove user {} from site {}: {}", userId, siteId, e.getMessage());
                // Continue with other users
            }
        }

        logger.info("Bulk removed {} users from site {}", removedCount, site.getCode());
        return removedCount;
    }

    // ===== VALIDATION AND BUSINESS RULES =====

    @Override
    @Transactional(readOnly = true)
    public boolean isSiteCodeAvailable(@NotNull String code) {
        return !siteRepository.existsByCode(code);
    }

    @Override
    public boolean validateSiteCode(@NotNull String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("Site code cannot be null or empty");
        }

        String trimmedCode = code.trim();

        if (trimmedCode.length() < 2 || trimmedCode.length() > 50) {
            throw new BusinessException("Site code must be between 2 and 50 characters");
        }

        if (!trimmedCode.matches("^[A-Z0-9_]+$")) {
            throw new BusinessException("Site code must contain only uppercase letters, numbers, and underscores");
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteSite(@NotNull Long siteId) {
        return countActiveUsersInSite(siteId) == 0;
    }

    @Override
    @Transactional(readOnly = true)
    public void validateSiteAccess(@NotNull Long userId, @NotNull Long siteId) {
        if (!hasUserAccessToSite(userId, siteId)) {
            throw new BusinessException("User does not have access to the specified site");
        }
    }

    // ===== STATISTICAL AND ANALYTICAL METHODS =====

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSiteStatistics(@NotNull Long siteId) {
        logger.debug("Getting statistics for site ID: {}", siteId);

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        Map<String, Object> stats = new HashMap<>();
        stats.put("siteId", siteId);
        stats.put("siteCode", site.getCode());
        stats.put("siteName", site.getName());
        stats.put("status", site.getStatus());
        stats.put("totalUsers", countTotalUsersInSite(siteId));
        stats.put("activeUsers", countActiveUsersInSite(siteId));
        stats.put("createdAt", site.getCreatedAt());
        stats.put("lastModified", site.getLastModified());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemSiteStatistics() {
        logger.debug("Getting system-wide site statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSites", countTotalSites());
        stats.put("activeSites", countSitesByStatus(SiteStatus.ACTIVE));
        stats.put("inactiveSites", countSitesByStatus(SiteStatus.INACTIVE));
        stats.put("maintenanceSites", countSitesByStatus(SiteStatus.MAINTENANCE));
        stats.put("suspendedSites", countSitesByStatus(SiteStatus.SUSPENDED));
        stats.put("statusDistribution", getSiteStatusDistribution());
        stats.put("timezoneDistribution", getTimezoneDistribution());
        stats.put("languageDistribution", getLanguageDistribution());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SiteStatus, Long> getSiteStatusDistribution() {
        List<Object[]> distribution = siteRepository.getSiteStatusDistribution();
        return distribution.stream()
                .collect(Collectors.toMap(
                        row -> (SiteStatus) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTimezoneDistribution() {
        List<Object[]> distribution = siteRepository.getTimezoneDistribution();
        return distribution.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getLanguageDistribution() {
        // Implementation would depend on having a specific query method
        // For now, return empty map as placeholder
        return new HashMap<>();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalSites() {
        return siteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countSitesByStatus(@NotNull SiteStatus status) {
        return siteRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsersInSite(@NotNull Long siteId) {
        return siteRepository.countUsersBySiteId(siteId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalUsersInSite(@NotNull Long siteId) {
        Site site = siteRepository.findById(siteId).orElse(null);
        if (site == null) {
            return 0;
        }
        return site.getUserSites().size();
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Build search specification from search criteria
     */
    private Specification<Site> buildSearchSpecification(SiteSearchCriteria criteria) {
        if (criteria == null || !criteria.hasSearchCriteria()) {
            return Specification.where(null);
        }

        return SiteSpecifications.buildSearchCriteria(
                criteria.getSearchTerm(),
                criteria.getStatuses(),
                criteria.getTimeZones(),
                criteria.getLanguageCodes(),
                criteria.getUserId(),
                criteria.getCreatedAfter(),
                criteria.getCreatedBefore()
        );
    }
}