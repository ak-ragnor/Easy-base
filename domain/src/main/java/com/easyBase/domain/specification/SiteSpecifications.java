package com.easyBase.domain.specification;

import com.easyBase.common.enums.SiteStatus;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Site Specifications for Dynamic Queries
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public class SiteSpecifications {

    /**
     * Sites with a specific status
     */
    public static Specification<Site> hasStatus(SiteStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Sites with multiple statuses
     */
    public static Specification<Site> hasStatusIn(List<SiteStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Sites with a specific timezone
     */
    public static Specification<Site> hasTimeZone(String timeZone) {
        return (root, query, criteriaBuilder) -> {
            if (timeZone == null || timeZone.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("timeZone"), timeZone.trim());
        };
    }

    /**
     * Sites with multiple timezones
     */
    public static Specification<Site> hasTimeZoneIn(List<String> timeZones) {
        return (root, query, criteriaBuilder) -> {
            if (timeZones == null || timeZones.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("timeZone").in(timeZones);
        };
    }

    /**
     * Sites with a specific language code
     */
    public static Specification<Site> hasLanguageCode(String languageCode) {
        return (root, query, criteriaBuilder) -> {
            if (languageCode == null || languageCode.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("languageCode"), languageCode.trim());
        };
    }

    /**
     * Sites with multiple language codes
     */
    public static Specification<Site> hasLanguageCodeIn(List<String> languageCodes) {
        return (root, query, criteriaBuilder) -> {
            if (languageCodes == null || languageCodes.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("languageCode").in(languageCodes);
        };
    }

    /**
     * Sites with name containing text (case-insensitive)
     */
    public static Specification<Site> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchTerm = "%" + name.trim().toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    searchTerm
            );
        };
    }

    /**
     * Sites with code containing text (case-insensitive)
     */
    public static Specification<Site> codeContains(String code) {
        return (root, query, criteriaBuilder) -> {
            if (code == null || code.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchTerm = "%" + code.trim().toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("code")),
                    searchTerm
            );
        };
    }

    /**
     * Sites with description containing text (case-insensitive)
     */
    public static Specification<Site> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchTerm = "%" + description.trim().toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    searchTerm
            );
        };
    }

    /**
     * Global search across name, code, and description
     */
    public static Specification<Site> globalSearch(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String term = "%" + searchTerm.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), term),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), term),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), term)
            );
        };
    }

    /**
     * Sites created after a specific date
     */
    public static Specification<Site> createdAfter(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("createdAt"), date);
        };
    }

    /**
     * Sites created before a specific date
     */
    public static Specification<Site> createdBefore(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThan(root.get("createdAt"), date);
        };
    }

    /**
     * Sites created between two dates
     */
    public static Specification<Site> createdBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate == null) {
                return criteriaBuilder.lessThan(root.get("createdAt"), endDate);
            }
            if (endDate == null) {
                return criteriaBuilder.greaterThan(root.get("createdAt"), startDate);
            }
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }

    /**
     * Sites modified after a specific date
     */
    public static Specification<Site> modifiedAfter(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("lastModified"), date);
        };
    }

    /**
     * Sites accessible by a specific user
     */
    public static Specification<Site> accessibleByUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            // Join with UserSite entity
            Join<Site, UserSite> userSiteJoin = root.join("userSites", JoinType.INNER);
            Join<UserSite, User> userJoin = userSiteJoin.join("user", JoinType.INNER);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(userJoin.get("id"), userId),
                    criteriaBuilder.equal(userSiteJoin.get("isActive"), true)
            );
        };
    }

    /**
     * Sites where user has admin access
     */
    public static Specification<Site> adminAccessibleByUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            // Join with UserSite entity
            Join<Site, UserSite> userSiteJoin = root.join("userSites", JoinType.INNER);
            Join<UserSite, User> userJoin = userSiteJoin.join("user", JoinType.INNER);

            // User has admin access if:
            // 1. Site-specific role is ADMIN or SUPER_ADMIN, OR
            // 2. Site role is null and global role is ADMIN or SUPER_ADMIN
            Predicate userIdMatch = criteriaBuilder.equal(userJoin.get("id"), userId);
            Predicate isActive = criteriaBuilder.equal(userSiteJoin.get("isActive"), true);

            Predicate siteRoleAdmin = userSiteJoin.get("siteRole").in(
                    com.easyBase.common.enums.UserRole.ADMIN
            );

            Predicate globalRoleAdmin = criteriaBuilder.and(
                    criteriaBuilder.isNull(userSiteJoin.get("siteRole")),
                    userJoin.get("role").in(
                            com.easyBase.common.enums.UserRole.ADMIN
                    )
            );

            return criteriaBuilder.and(
                    userIdMatch,
                    isActive,
                    criteriaBuilder.or(siteRoleAdmin, globalRoleAdmin)
            );
        };
    }

    /**
     * Sites with a minimum number of users
     */
    public static Specification<Site> hasMinimumUsers(int minimumUserCount) {
        return (root, query, criteriaBuilder) -> {
            if (minimumUserCount <= 0) {
                return criteriaBuilder.conjunction();
            }

            // Subquery to count active users per site
            Subquery<Long> userCountSubquery = query.subquery(Long.class);
            Root<UserSite> userSiteRoot = userCountSubquery.from(UserSite.class);

            userCountSubquery.select(criteriaBuilder.count(userSiteRoot.get("user")))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(userSiteRoot.get("site"), root),
                                    criteriaBuilder.equal(userSiteRoot.get("isActive"), true)
                            )
                    );

            return criteriaBuilder.greaterThanOrEqualTo(userCountSubquery, (long) minimumUserCount);
        };
    }

    /**
     * Sites with a maximum number of users
     */
    public static Specification<Site> hasMaximumUsers(int maximumUserCount) {
        return (root, query, criteriaBuilder) -> {
            if (maximumUserCount < 0) {
                return criteriaBuilder.conjunction();
            }

            // Subquery to count active users per site
            Subquery<Long> userCountSubquery = query.subquery(Long.class);
            Root<UserSite> userSiteRoot = userCountSubquery.from(UserSite.class);

            userCountSubquery.select(criteriaBuilder.count(userSiteRoot.get("user")))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(userSiteRoot.get("site"), root),
                                    criteriaBuilder.equal(userSiteRoot.get("isActive"), true)
                            )
                    );

            return criteriaBuilder.lessThanOrEqualTo(userCountSubquery, (long) maximumUserCount);
        };
    }

    /**
     * Operational sites (active or maintenance status)
     */
    public static Specification<Site> isOperational() {
        List<SiteStatus> operationalStatuses = List.of(SiteStatus.ACTIVE, SiteStatus.MAINTENANCE);
        return hasStatusIn(operationalStatuses);
    }

    /**
     * Accessible sites for users (active or maintenance)
     */
    public static Specification<Site> isAccessible() {
        List<SiteStatus> accessibleStatuses = List.of(SiteStatus.ACTIVE, SiteStatus.MAINTENANCE);
        return hasStatusIn(accessibleStatuses);
    }

    /**
     * Recently created sites (within last N days)
     */
    public static Specification<Site> recentlyCreated(int days) {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(days);
        return createdAfter(cutoffDate);
    }

    /**
     * Recently modified sites (within last N days)
     */
    public static Specification<Site> recentlyModified(int days) {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(days);
        return modifiedAfter(cutoffDate);
    }

    /**
     * Build a complex search specification from multiple criteria
     *
     * @param searchTerm    Global search term (optional)
     * @param statuses      List of statuses to include (optional)
     * @param timeZones     List of timezones to include (optional)
     * @param languageCodes List of language codes to include (optional)
     * @param userId        User ID for access filtering (optional)
     * @param createdAfter  Created after date (optional)
     * @param createdBefore Created before date (optional)
     * @return Combined specification
     */
    public static Specification<Site> buildSearchCriteria(
            String searchTerm,
            List<SiteStatus> statuses,
            List<String> timeZones,
            List<String> languageCodes,
            Long userId,
            ZonedDateTime createdAfter,
            ZonedDateTime createdBefore) {

        List<Specification<Site>> specifications = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            specifications.add(globalSearch(searchTerm));
        }

        if (statuses != null && !statuses.isEmpty()) {
            specifications.add(hasStatusIn(statuses));
        }

        if (timeZones != null && !timeZones.isEmpty()) {
            specifications.add(hasTimeZoneIn(timeZones));
        }

        if (languageCodes != null && !languageCodes.isEmpty()) {
            specifications.add(hasLanguageCodeIn(languageCodes));
        }

        if (userId != null) {
            specifications.add(accessibleByUser(userId));
        }

        if (createdAfter != null || createdBefore != null) {
            specifications.add(createdBetween(createdAfter, createdBefore));
        }

        // Combine all specifications with AND logic
        return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
    }

    /**
     * Build admin-focused search criteria
     *
     * @param searchTerm Global search term (optional)
     * @param statuses   List of statuses to include (optional)
     * @param userId     Admin user ID for access filtering (optional)
     * @return Combined specification for admin searches
     */
    public static Specification<Site> buildAdminSearchCriteria(
            String searchTerm,
            List<SiteStatus> statuses,
            Long userId) {

        List<Specification<Site>> specifications = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            specifications.add(globalSearch(searchTerm));
        }

        if (statuses != null && !statuses.isEmpty()) {
            specifications.add(hasStatusIn(statuses));
        }

        if (userId != null) {
            specifications.add(adminAccessibleByUser(userId));
        }

        return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
    }
}