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

        dto.setId(site.getId());
        dto.setCreatedAt(site.getCreatedAt());
        dto.setLastModified(site.getLastModified());

        dto.setCode(site.getCode());
        dto.setName(site.getName());
        dto.setDescription(site.getDescription());
        dto.setStatus(site.getStatus());
        dto.setTimeZone(site.getTimeZone());
        dto.setLanguageCode(site.getLanguageCode());

        if (site.getStatus() != null) {
            dto.setAccessible(site.isAccessible());
            dto.setModifiable(site.isModifiable());
            dto.setOperational(site.isOperational());
        }

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

        dto.setCreatedAt(userSite.getCreatedAt());
        dto.setLastModified(userSite.getLastModified());
        dto.setVersion(userSite.getVersion());

        if (userSite.getUser() != null) {
            dto.setUser(userMapper.toDTO(userSite.getUser()));
        }

        if (userSite.getSite() != null) {
            dto.setSite(toSimpleDTO(userSite.getSite()));
        }

        dto.setSiteRole(userSite.getSiteRole());
        dto.setEffectiveRole(userSite.getEffectiveRole());
        dto.setIsActive(userSite.getIsActive());
        dto.setAccessGrantedAt(userSite.getAccessGrantedAt());
        dto.setAccessRevokedAt(userSite.getAccessRevokedAt());
        dto.setGrantedByUserId(userSite.getGrantedByUserId());
        dto.setRevokedByUserId(userSite.getRevokedByUserId());
        dto.setNotes(userSite.getNotes());

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

        dto.setId(site.getId());
        dto.setCreatedAt(site.getCreatedAt());
        dto.setLastModified(site.getLastModified());

        dto.setCode(site.getCode());
        dto.setName(site.getName());
        dto.setDescription(site.getDescription());
        dto.setStatus(site.getStatus());
        dto.setTimeZone(site.getTimeZone());
        dto.setLanguageCode(site.getLanguageCode());

        if (site.getStatus() != null) {
            dto.setAccessible(site.isAccessible());
            dto.setModifiable(site.isModifiable());
            dto.setOperational(site.isOperational());
        }

        return dto;
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

        return userSite;
    }
}