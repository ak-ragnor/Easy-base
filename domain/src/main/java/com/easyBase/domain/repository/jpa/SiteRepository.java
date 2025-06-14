package com.easyBase.domain.repository.jpa;

import com.easyBase.common.enums.SiteStatus;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Site Repository Interface
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface SiteRepository extends BaseRepository<Site, Long> {

    /**
     * Find site by unique code
     * Performance-critical method with index support
     *
     * @param code Site code to search for
     * @return Optional Site entity
     */
    @Query("SELECT s FROM Site s WHERE s.code = :code")
    Optional<Site> findByCode(@Param("code") String code);

    /**
     * Find site by code (case-insensitive)
     * Useful for user input scenarios
     *
     * @param code Site code to search for (case-insensitive)
     * @return Optional Site entity
     */
    @Query("SELECT s FROM Site s WHERE UPPER(s.code) = UPPER(:code)")
    Optional<Site> findByCodeIgnoreCase(@Param("code") String code);

    /**
     * Check if site exists by code
     * Optimized existence check without loading full entity
     *
     * @param code Site code to check
     * @return true if site exists
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Site s WHERE s.code = :code")
    boolean existsByCode(@Param("code") String code);

    /**
     * Find sites by status
     * Ordered by name for consistent display
     *
     * @param status Site status to filter by
     * @return List of sites with specified status
     */
    @Query("SELECT s FROM Site s WHERE s.status = :status ORDER BY s.name")
    List<Site> findByStatus(@Param("status") SiteStatus status);

    /**
     * Find sites by status with pagination
     *
     * @param status   Site status to filter by
     * @param pageable Pagination parameters
     * @return Page of sites with specified status
     */
    @Query("SELECT s FROM Site s WHERE s.status = :status")
    Page<Site> findByStatus(@Param("status") SiteStatus status, Pageable pageable);

    /**
     * Find sites by multiple statuses
     * Useful for filtering operational vs non-operational sites
     *
     * @param statuses List of statuses to include
     * @return List of sites with any of the specified statuses
     */
    @Query("SELECT s FROM Site s WHERE s.status IN :statuses ORDER BY s.name")
    List<Site> findByStatusIn(@Param("statuses") List<SiteStatus> statuses);

    /**
     * Find all active sites
     * Most commonly used query for operational sites
     *
     * @return List of active sites
     */
    @Query("SELECT s FROM Site s WHERE s.status = com.easyBase.common.enums.SiteStatus.ACTIVE ORDER BY s.name")
    List<Site> findActiveSites();

    /**
     * Find all accessible sites (active or maintenance)
     * Sites that users can potentially access
     *
     * @return List of accessible sites
     */
    @Query("SELECT s FROM Site s WHERE s.status IN (com.easyBase.common.enums.SiteStatus.ACTIVE, com.easyBase.common.enums.SiteStatus.MAINTENANCE) ORDER BY s.name")
    List<Site> findAccessibleSites();

    /**
     * Find sites by timezone
     * Useful for scheduled operations and reporting
     *
     * @param timeZone Timezone identifier
     * @return List of sites in the specified timezone
     */
    @Query("SELECT s FROM Site s WHERE s.timeZone = :timeZone ORDER BY s.name")
    List<Site> findByTimeZone(@Param("timeZone") String timeZone);

    /**
     * Find sites by language code
     * Useful for localization and content management
     *
     * @param languageCode Language code (e.g., "en", "es")
     * @return List of sites using the specified language
     */
    @Query("SELECT s FROM Site s WHERE s.languageCode = :languageCode ORDER BY s.name")
    List<Site> findByLanguageCode(@Param("languageCode") String languageCode);

    /**
     * Find sites created after a specific date
     * Enhanced version of inherited method with site-specific ordering
     *
     * @param date     Date threshold
     * @param pageable Pagination parameters
     * @return Page of recently created sites
     */
    @Query("SELECT s FROM Site s WHERE s.createdAt > :date ORDER BY s.createdAt DESC, s.name")
    Page<Site> findRecentlyCreated(@Param("date") ZonedDateTime date, Pageable pageable);

    /**
     * Search sites by name (partial match, case-insensitive)
     *
     * @param name     Partial site name to search for
     * @param pageable Pagination parameters
     * @return Page of sites matching the name criteria
     */
    @Query("SELECT s FROM Site s WHERE UPPER(s.name) LIKE UPPER(CONCAT('%', :name, '%')) ORDER BY s.name")
    Page<Site> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Search sites by code (partial match, case-insensitive)
     *
     * @param code     Partial site code to search for
     * @param pageable Pagination parameters
     * @return Page of sites matching the code criteria
     */
    @Query("SELECT s FROM Site s WHERE UPPER(s.code) LIKE UPPER(CONCAT('%', :code, '%')) ORDER BY s.code")
    Page<Site> findByCodeContaining(@Param("code") String code, Pageable pageable);

    /**
     * Count sites by status
     * Optimized counting without loading entities
     *
     * @param status Site status to count
     * @return Number of sites with the specified status
     */
    @Query("SELECT COUNT(s) FROM Site s WHERE s.status = :status")
    long countByStatus(@Param("status") SiteStatus status);

    /**
     * Count sites by timezone
     * Useful for distribution analysis
     *
     * @param timeZone Timezone identifier
     * @return Number of sites in the specified timezone
     */
    @Query("SELECT COUNT(s) FROM Site s WHERE s.timeZone = :timeZone")
    long countByTimeZone(@Param("timeZone") String timeZone);

    /**
     * Count sites by language
     * Useful for localization planning
     *
     * @param languageCode Language code
     * @return Number of sites using the specified language
     */
    @Query("SELECT COUNT(s) FROM Site s WHERE s.languageCode = :languageCode")
    long countByLanguageCode(@Param("languageCode") String languageCode);

    /**
     * Count active sites
     * Most commonly used statistic
     *
     * @return Number of active sites
     */
    @Query("SELECT COUNT(s) FROM Site s WHERE s.status = com.easyBase.common.enums.SiteStatus.ACTIVE")
    long countActiveSites();

    /**
     * Count accessible sites
     *
     * @return Number of sites users can access
     */
    @Query("SELECT COUNT(s) FROM Site s WHERE s.status IN (com.easyBase.common.enums.SiteStatus.ACTIVE, com.easyBase.common.enums.SiteStatus.MAINTENANCE)")
    long countAccessibleSites();

    /**
     * Get site status distribution
     * Returns count of sites by status for dashboard analytics
     *
     * @return List of arrays containing [status, count]
     */
    @Query("SELECT s.status, COUNT(s) FROM Site s GROUP BY s.status ORDER BY COUNT(s) DESC")
    List<Object[]> getSiteStatusDistribution();

    /**
     * Get timezone distribution
     * Returns count of sites by timezone
     *
     * @return List of arrays containing [timezone, count]
     */
    @Query("SELECT s.timeZone, COUNT(s) FROM Site s GROUP BY s.timeZone ORDER BY COUNT(s) DESC")
    List<Object[]> getTimezoneDistribution();

    /**
     * Bulk update site status
     * Administrative operation for mass status changes
     *
     * @param siteIds   List of site IDs to update
     * @param newStatus New status to set
     * @return Number of updated sites
     */
    @Modifying
    @Query("UPDATE Site s SET s.status = :newStatus, s.lastModified = CURRENT_TIMESTAMP WHERE s.id IN :siteIds")
    int bulkUpdateStatus(@Param("siteIds") List<Long> siteIds, @Param("newStatus") SiteStatus newStatus);

    /**
     * Bulk update timezone
     * Administrative operation for timezone changes
     *
     * @param siteIds     List of site IDs to update
     * @param newTimeZone New timezone to set
     * @return Number of updated sites
     */
    @Modifying
    @Query("UPDATE Site s SET s.timeZone = :newTimeZone, s.lastModified = CURRENT_TIMESTAMP WHERE s.id IN :siteIds")
    int bulkUpdateTimeZone(@Param("siteIds") List<Long> siteIds, @Param("newTimeZone") String newTimeZone);

    /**
     * Bulk update language code
     * Administrative operation for language changes
     *
     * @param siteIds         List of site IDs to update
     * @param newLanguageCode New language code to set
     * @return Number of updated sites
     */
    @Modifying
    @Query("UPDATE Site s SET s.languageCode = :newLanguageCode, s.lastModified = CURRENT_TIMESTAMP WHERE s.id IN :siteIds")
    int bulkUpdateLanguageCode(@Param("siteIds") List<Long> siteIds, @Param("newLanguageCode") String newLanguageCode);

    /**
     * Find sites for a specific user
     *
     * @param userId User ID to search for
     * @return List of sites the user has access to
     */
    @Query("SELECT DISTINCT us.site FROM UserSite us WHERE us.user.id = :userId AND us.isActive = true ORDER BY us.site.name")
    List<Site> findSitesByUserId(@Param("userId") Long userId);

    /**
     * Find sites where user has admin access
     *
     * @param userId User ID to search for
     * @return List of sites where the user has admin access
     */
    @Query("SELECT DISTINCT us.site FROM UserSite us WHERE us.user.id = :userId AND us.isActive = true AND " +
            "(us.siteRole IN (com.easyBase.common.enums.UserRole.ADMIN, com.easyBase.common.enums.UserRole.SUPER_ADMIN) OR " +
            "(us.siteRole IS NULL AND us.user.role IN (com.easyBase.common.enums.UserRole.ADMIN, com.easyBase.common.enums.UserRole.SUPER_ADMIN))) " +
            "ORDER BY us.site.name")
    List<Site> findAdminSitesByUserId(@Param("userId") Long userId);

    /**
     * Count users in a site
     *
     * @param siteId Site ID to count users for
     * @return Number of active users in the site
     */
    @Query("SELECT COUNT(DISTINCT us.user) FROM UserSite us WHERE us.site.id = :siteId AND us.isActive = true")
    long countUsersBySiteId(@Param("siteId") Long siteId);
}