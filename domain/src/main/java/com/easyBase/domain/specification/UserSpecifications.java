package com.easyBase.domain.specification;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.user.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise User Specifications for Dynamic Queries - Hybrid Approach
 *
 * This class provides reusable, composable query specifications for User entities.
 * Designed to work alongside custom @Query methods in UserRepository.
 *
 * HYBRID STRATEGY:
 * - Use specifications for complex, multi-criteria searches
 * - Use custom @Query methods for simple, performance-critical operations
 * - Combine both approaches for maximum flexibility and performance
 *
 * @author Enterprise Team
 * @version 2.0 - Hybrid Approach
 * @since 1.0
 */
public class UserSpecifications {

    // ===== BASIC SPECIFICATIONS =====

    /**
     * Users with a specific role
     */
    public static Specification<User> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("role"), role);
        };
    }

    /**
     * Users with multiple roles
     */
    public static Specification<User> hasRoleIn(List<UserRole> roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("role").in(roles);
        };
    }

    /**
     * Users with a specific status
     */
    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Users with multiple statuses
     */
    public static Specification<User> hasStatusIn(List<UserStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Active users only - commonly used specification
     */
    public static Specification<User> isActive() {
        return hasStatus(UserStatus.ACTIVE);
    }

    /**
     * Inactive users (all statuses except ACTIVE)
     */
    public static Specification<User> isInactive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), UserStatus.ACTIVE);
    }

    // ===== SEARCH SPECIFICATIONS =====

    /**
     * Search users by name or email (case-insensitive)
     * Perfect for search boxes in UI
     */
    public static Specification<User> hasNameOrEmailContaining(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), likePattern);
            Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), likePattern);

            return criteriaBuilder.or(namePredicate, emailPredicate);
        };
    }

    /**
     * Exact email match (case-insensitive)
     * Note: For authentication, use the custom @Query in repository for performance
     */
    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("email")),
                    email.toLowerCase());
        };
    }

    // ===== TIMEZONE SPECIFICATIONS =====

    /**
     * Users in a specific timezone
     */
    public static Specification<User> hasTimezone(String timezone) {
        return (root, query, criteriaBuilder) -> {
            if (timezone == null || timezone.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userTimezone"), timezone);
        };
    }

    /**
     * Users in multiple timezones
     */
    public static Specification<User> hasTimezoneIn(List<String> timezones) {
        return (root, query, criteriaBuilder) -> {
            if (timezones == null || timezones.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("userTimezone").in(timezones);
        };
    }

    // ===== DATE/TIME SPECIFICATIONS =====

    /**
     * Users created after a specific date
     */
    public static Specification<User> createdAfter(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("createdAt"), date);
        };
    }

    /**
     * Users created before a specific date
     */
    public static Specification<User> createdBefore(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThan(root.get("createdAt"), date);
        };
    }

    /**
     * Users created within a date range
     */
    public static Specification<User> createdBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }
            if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            }
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }

    // ===== BUSINESS LOGIC SPECIFICATIONS =====

    /**
     * Administrative users (ADMIN and MANAGER roles)
     */
    public static Specification<User> isAdministrative() {
        return hasRoleIn(List.of(UserRole.ADMIN, UserRole.MANAGER));
    }

    /**
     * Users who can manage other users (active admins/managers)
     */
    public static Specification<User> canManageUsers() {
        return isActive().and(isAdministrative());
    }

    /**
     * Users who can login (active status)
     */
    public static Specification<User> canLogin() {
        return isActive(); // Only active users can login in our business logic
    }

    /**
     * Users requiring attention (locked, suspended, etc.)
     */
    public static Specification<User> requiresAttention() {
        List<UserStatus> attentionStatuses = List.of(
                UserStatus.LOCKED,
                UserStatus.SUSPENDED,
                UserStatus.DISABLED
        );
        return hasStatusIn(attentionStatuses);
    }

    // ===== DYNAMIC SEARCH BUILDER =====

    /**
     * 🚀 MAIN DYNAMIC SEARCH METHOD
     *
     * This method builds a specification based on provided search criteria.
     * Only applies conditions for non-null/non-empty criteria.
     *
     * Perfect for API endpoints with optional search parameters.
     */
    public static Specification<User> buildSearchSpecification(String searchTerm,
                                                               UserRole role,
                                                               UserStatus status,
                                                               String timezone,
                                                               ZonedDateTime createdAfter,
                                                               ZonedDateTime createdBefore) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Text search across name and email
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate));
            }

            // Role filter
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            // Status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Timezone filter
            if (timezone != null && !timezone.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("userTimezone"), timezone));
            }

            // Date filters
            if (createdAfter != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("createdAt"), createdAfter));
            }

            if (createdBefore != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("createdAt"), createdBefore));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ===== UTILITY METHODS =====

    /**
     * Combine multiple specifications with AND logic
     */
    @SafeVarargs
    public static Specification<User> and(Specification<User>... specifications) {
        Specification<User> result = Specification.where(null);
        for (Specification<User> spec : specifications) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

    /**
     * Combine multiple specifications with OR logic
     */
    @SafeVarargs
    public static Specification<User> or(Specification<User>... specifications) {
        Specification<User> result = null;
        for (Specification<User> spec : specifications) {
            if (spec != null) {
                result = (result == null) ? spec : result.or(spec);
            }
        }
        return result != null ? result : Specification.where(null);
    }
}