package com.easyBase.service.mapper;

import com.easyBase.common.dto.site.CreateSiteRequest;
import com.easyBase.common.dto.site.SiteDTO;
import com.easyBase.common.dto.site.UpdateSiteRequest;
import com.easyBase.common.dto.site.UserSiteDTO;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Site Mapper
 *
 * Handles conversion between Site entities and DTOs.
 * Provides mapping methods for all site-related objects including
 * Site entities, UserSite relationships, and various request/response DTOs.
 *
 * Features:
 * - Entity to DTO conversion with computed fields
 * - DTO to Entity conversion for persistence
 * - Request DTO to Entity mapping for create/update operations
 * - UserSite relationship mapping with role resolution
 * - Null-safe operations with proper validation
 * - Performance optimized for batch operations
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class SiteMapper {

    @Autowired
    private UserMapper userMapper;

    // ===== SITE ENTITY <-> DTO MAPPING =====

    /**
     * Convert Site entity to SiteDTO
     *
     * @param site Site entity to convert
     * @return SiteDTO or null if input is null
     */
    public SiteDTO toDTO(Site site) {
        if (site == null) {
            return null;
        }

        SiteDTO dto = new SiteDTO();

        // Copy base fields
        dto.setId(site.getId());
        dto.setCreatedAt(site.getCreatedAt());
        dto.setLastModified(site.getLastModified());

        // Copy site-specific fields
        dto.setCode(site.getCode());
        dto.setName(site.getName());
        dto.setDescription(site.getDescription());
        dto.setStatus(site.getStatus());
        dto.setTimeZone(site.getTimeZone());
        dto.setLanguageCode(site.getLanguageCode());

        // Set computed flags based on status
        if (site.getStatus() != null) {
            dto.setAccessible(site.isAccessible());
            dto.setModifiable(site.isModifiable());
            dto.setOperational(site.isOperational());
        }

        // Count users if userSites collection is available and loaded
        if (site.getUserSites() != null) {
            long activeUsers = site.getUserSites().stream()
                    .filter(us -> us.getIsActive() != null && us.getIsActive())
                    .count();
            long totalUsers = site.getUserSites().size();

            dto.setActiveUsersCount(activeUsers);
            dto.setTotalUsersCount(totalUsers);
        }

        return dto;
    }

    /**
     * Convert CreateSiteRequest to Site entity
     *
     * @param request CreateSiteRequest to convert
     * @return Site entity or null if input is null
     */
    public Site toEntity(CreateSiteRequest request) {
        if (request == null) {
            return null;
        }

        Site site = new Site();
        site.setCode(request.getCode());
        site.setName(request.getName());
        site.setDescription(request.getDescription());
        site.setStatus(request.getStatus());
        site.setTimeZone(request.getTimeZone());
        site.setLanguageCode(request.getLanguageCode());

        return site;
    }

    /**
     * Update Site entity from UpdateSiteRequest
     *
     * @param site    Existing Site entity to update
     * @param request UpdateSiteRequest with new values
     * @return Updated Site entity or null if either input is null
     */
    public Site updateEntity(Site site, UpdateSiteRequest request) {
        if (site == null || request == null) {
            return site;
        }

        site.setName(request.getName());
        site.setDescription(request.getDescription());
        site.setStatus(request.getStatus());
        site.setTimeZone(request.getTimeZone());
        site.setLanguageCode(request.getLanguageCode());

        return site;
    }

    /**
     * Convert list of Site entities to list of SiteDTOs
     *
     * @param sites List of Site entities
     * @return List of SiteDTOs or empty list if input is null
     */
    public List<SiteDTO> toDTOList(List<Site> sites) {
        if (sites == null) {
            return List.of();
        }

        return sites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert set of Site entities to list of SiteDTOs
     *
     * @param sites Set of Site entities
     * @return List of SiteDTOs or empty list if input is null
     */
    public List<SiteDTO> toDTOList(Set<Site> sites) {
        if (sites == null) {
            return List.of();
        }

        return sites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== USER-SITE RELATIONSHIP MAPPING =====

    /**
     * Convert UserSite entity to UserSiteDTO
     *
     * @param userSite UserSite entity to convert
     * @return UserSiteDTO or null if input is null
     */
    public UserSiteDTO toDTO(UserSite userSite) {
        if (userSite == null) {
            return null;
        }

        UserSiteDTO dto = new UserSiteDTO();

        // Copy audit fields (UserSite doesn't extend AuditableEntity)
        dto.setCreatedAt(userSite.getCreatedAt());
        dto.setLastModified(userSite.getLastModified());
        dto.setVersion(userSite.getVersion());

        // Convert related entities (avoiding circular references)
        if (userSite.getUser() != null) {
            dto.setUser(userMapper.toDTO(userSite.getUser()));
        }

        if (userSite.getSite() != null) {
            dto.setSite(toSimpleDTO(userSite.getSite()));
        }

        // Copy relationship fields
        dto.setSiteRole(userSite.getSiteRole());
        dto.setEffectiveRole(userSite.getEffectiveRole());
        dto.setIsActive(userSite.getIsActive());
        dto.setAccessGrantedAt(userSite.getAccessGrantedAt());
        dto.setAccessRevokedAt(userSite.getAccessRevokedAt());
        dto.setGrantedByUserId(userSite.getGrantedByUserId());
        dto.setRevokedByUserId(userSite.getRevokedByUserId());
        dto.setNotes(userSite.getNotes());

        // Set computed flags
        dto.setValidAccess(userSite.isValidAccess());
        dto.setAdminAccess(userSite.hasAdminAccess());

        return dto;
    }

    /**
     * Convert UserSite entity to UserSiteDTO with user names resolved
     *
     * @param userSite           UserSite entity to convert
     * @param grantedByUserName  Name of user who granted access
     * @param revokedByUserName  Name of user who revoked access
     * @return UserSiteDTO or null if input is null
     */
    public UserSiteDTO toDTO(UserSite userSite, String grantedByUserName, String revokedByUserName) {
        UserSiteDTO dto = toDTO(userSite);
        if (dto != null) {
            dto.setGrantedByUserName(grantedByUserName);
            dto.setRevokedByUserName(revokedByUserName);
        }
        return dto;
    }

    /**
     * Convert list of UserSite entities to list of UserSiteDTOs
     *
     * @param userSites List of UserSite entities
     * @return List of UserSiteDTOs or empty list if input is null
     */
    public List<UserSiteDTO> toUserSiteDTOList(List<UserSite> userSites) {
        if (userSites == null) {
            return List.of();
        }

        return userSites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert set of UserSite entities to list of UserSiteDTOs
     *
     * @param userSites Set of UserSite entities
     * @return List of UserSiteDTOs or empty list if input is null
     */
    public List<UserSiteDTO> toUserSiteDTOList(Set<UserSite> userSites) {
        if (userSites == null) {
            return List.of();
        }

        return userSites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== SIMPLIFIED DTO METHODS (to avoid circular references) =====

    /**
     * Convert Site entity to simplified SiteDTO (without user counts)
     * Used to avoid circular references in UserSite mappings
     *
     * @param site Site entity to convert
     * @return Simplified SiteDTO or null if input is null
     */
    public SiteDTO toSimpleDTO(Site site) {
        if (site == null) {
            return null;
        }

        SiteDTO dto = new SiteDTO();

        // Copy base fields
        dto.setId(site.getId());
        dto.setCreatedAt(site.getCreatedAt());
        dto.setLastModified(site.getLastModified());

        // Copy site-specific fields
        dto.setCode(site.getCode());
        dto.setName(site.getName());
        dto.setDescription(site.getDescription());
        dto.setStatus(site.getStatus());
        dto.setTimeZone(site.getTimeZone());
        dto.setLanguageCode(site.getLanguageCode());

        // Set computed flags based on status
        if (site.getStatus() != null) {
            dto.setAccessible(site.isAccessible());
            dto.setModifiable(site.isModifiable());
            dto.setOperational(site.isOperational());
        }

        // Don't count users in simple DTO to avoid lazy loading issues

        return dto;
    }

    // ===== BULK MAPPING METHODS FOR PERFORMANCE =====

    /**
     * Convert list of Site entities to SiteDTOs with user counts
     * Optimized for batch operations by calculating user counts efficiently
     *
     * @param sites            List of Site entities
     * @param userCountsMap    Map of site ID to active user count
     * @param totalCountsMap   Map of site ID to total user count
     * @return List of SiteDTOs with user counts populated
     */
    public List<SiteDTO> toDTOListWithUserCounts(
            List<Site> sites,
            java.util.Map<Long, Long> userCountsMap,
            java.util.Map<Long, Long> totalCountsMap) {

        if (sites == null) {
            return List.of();
        }

        return sites.stream()
                .map(site -> {
                    SiteDTO dto = toDTO(site);
                    if (dto != null && userCountsMap != null) {
                        dto.setActiveUsersCount(userCountsMap.getOrDefault(site.getId(), 0L));
                    }
                    if (dto != null && totalCountsMap != null) {
                        dto.setTotalUsersCount(totalCountsMap.getOrDefault(site.getId(), 0L));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ===== UTILITY METHODS =====

    /**
     * Create a basic Site entity with required fields
     *
     * @param code         Site code
     * @param name         Site name
     * @param status       Site status
     * @param timeZone     Site timezone
     * @param languageCode Site language code
     * @return Site entity with basic fields set
     */
    public Site createBasicSite(String code, String name,
                                com.easyBase.common.enums.SiteStatus status,
                                String timeZone, String languageCode) {
        Site site = new Site();
        site.setCode(code);
        site.setName(name);
        site.setStatus(status);
        site.setTimeZone(timeZone);
        site.setLanguageCode(languageCode);
        return site;
    }

    /**
     * Create a UserSite relationship
     *
     * @param user           User entity
     * @param site           Site entity
     * @param siteRole       Site-specific role (optional)
     * @param grantedByUserId ID of user granting access
     * @return UserSite entity
     */
    public UserSite createUserSite(User user, Site site,
                                   com.easyBase.common.enums.UserRole siteRole,
                                   Long grantedByUserId) {
        UserSite userSite = new UserSite(user, site);
        userSite.setSiteRole(siteRole);
        userSite.setGrantedByUserId(grantedByUserId);
        ZonedDateTime now = ZonedDateTime.now();
        userSite.setAccessGrantedAt(now);
        // Audit fields are set in constructor
        return userSite;
    }

    /**
     * Check if DTO represents the same entity as the given site
     *
     * @param dto  SiteDTO to compare
     * @param site Site entity to compare
     * @return true if they represent the same entity
     */
    public boolean isSameEntity(SiteDTO dto, Site site) {
        if (dto == null || site == null) {
            return false;
        }

        return dto.getId() != null &&
                dto.getId().equals(site.getId()) &&
                dto.getCode() != null &&
                dto.getCode().equals(site.getCode());
    }

    /**
     * Check if two SiteDTOs represent the same entity
     *
     * @param dto1 First SiteDTO
     * @param dto2 Second SiteDTO
     * @return true if they represent the same entity
     */
    public boolean isSameEntity(SiteDTO dto1, SiteDTO dto2) {
        if (dto1 == null || dto2 == null) {
            return false;
        }

        return dto1.getId() != null &&
                dto1.getId().equals(dto2.getId()) &&
                dto1.getCode() != null &&
                dto1.getCode().equals(dto2.getCode());
    }
}