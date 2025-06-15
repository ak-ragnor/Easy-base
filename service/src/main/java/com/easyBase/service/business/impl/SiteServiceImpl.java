package com.easyBase.service.business.impl;

import com.easyBase.common.dto.site.*;
import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.site.SiteRepository;
import com.easyBase.domain.repository.jpa.site.UserSiteRepository;
import com.easyBase.domain.repository.jpa.user.UserRepository;
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

import java.util.*;

/**
 * Site Service Implementation
 *
 * @author Akhash R
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

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Override
    public SiteDTO createSite(@Valid @NotNull CreateSiteRequest request) {
        logger.debug("Creating new site with code: {}", request.getCode());

        if (!isSiteCodeAvailable(request.getCode())) {
            throw new BusinessException("Site code '" + request.getCode() + "' is already in use");
        }

        validateSiteCode(request.getCode());

        Site site = siteMapper.toEntity(request);

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
    public void deleteSite(@NotNull Long id, boolean force) {
        logger.debug("Deleting site with ID: {}, force: {}", id, force);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + id));

        if (!force && !canDeleteSite(id)) {
            long activeUsers = countActiveUsersInSite(id);
            throw new BusinessException("Cannot delete site '" + site.getCode() +
                    "' as it has " + activeUsers + " active users. Use force=true to delete anyway.");
        }

        siteRepository.delete(site);
        logger.info("Deleted site: {} with ID: {}", site.getCode(), site.getId());
    }

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
    public UserSiteDTO addUserToSite(Long siteId, Long userId, UserRole siteRole,
                                     Long grantedByUserId, String notes) {

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with ID: " + siteId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (hasUserAccessToSite(userId, siteId)) {
            throw new BusinessException("User already has access to site: " + site.getCode());
        }

        Optional<UserSite> existingUserSite = userSiteRepository.findByUserIdAndSiteId(userId, siteId);

        UserSite userSite;

        if (existingUserSite.isPresent()) {
            userSite = existingUserSite.get();
            logger.info("Updating existing user-site association for user {} and site {}", userId, siteId);

            userSite.updateAssociation(siteRole, notes, grantedByUserId);
            userSite.setIsActive(true);

        } else {
            logger.info("Creating new user-site association for user {} and site {}", userId, siteId);

            userSite = new UserSite(user, site, siteRole, grantedByUserId);
        }

        userSite = userSiteRepository.save(userSite);

        logger.info("Successfully saved user-site association for user {} and site {}", userId, siteId);

        UserSite userSiteWithDetails = userSiteRepository.findByUserIdAndSiteIdWithDetails(userId, siteId)
                .orElse(userSite);

        return siteMapper.toDTO(userSiteWithDetails);
    }

    @Override
    @Transactional
    public void removeUserFromSite(Long siteId, Long userId) {
        if (userSiteRepository.existsByUserIdAndSiteIdAndIsActive(userId, siteId, true)) {
            userSiteRepository.deactivateUserSite(userId, siteId);
            logger.info("Deactivated user {} from site {}", userId, siteId);
        } else {
            throw new ResourceNotFoundException(
                    String.format("Active association between user %d and site %d not found", userId, siteId));
        }
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
    public boolean canDeleteSite(@NotNull Long siteId) {
        return countActiveUsersInSite(siteId) == 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsersInSite(@NotNull Long siteId) {
        return siteRepository.countUsersBySiteId(siteId);
    }

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