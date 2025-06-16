package com.easyBase.domain.repository.jpa.user;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.base.BaseRepository;
import com.easyBase.domain.specification.UserSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🚀 Hybrid User Repository - Best of Both Worlds
 *
 * This repository demonstrates the optimal enterprise approach:
 * ✅ Custom @Query methods for performance-critical operations
 * ✅ Dynamic Specifications for flexible, complex searches
 * ✅ Clean separation of concerns
 * ✅ Maximum maintainability and flexibility
 *
 * DECISION MATRIX:
 *
 * Use Custom @Query When:
 * ✅ Authentication/login queries (performance critical)
 * ✅ Simple, frequently used queries (count, exists)
 * ✅ Bulk operations (mass updates, deletes)
 * ✅ Complex SQL with joins, aggregations, window functions
 *
 * Use Dynamic Specifications When:
 * ✅ Search with optional parameters (API endpoints)
 * ✅ Complex business logic combinations
 * ✅ Report generation with filters
 * ✅ Multi-criteria filtering
 *
 * @author Enterprise Team
 * @version 2.0 - Hybrid Approach
 * @since 1.0
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    // ===== CUSTOM QUERIES (Performance & Simplicity) =====

    /**
     * 🔥 CRITICAL: User authentication by email
     * Custom query for maximum performance in login flow
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 🔥 CRITICAL: Email uniqueness check
     * Used during user registration and updates
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * 🔥 CRITICAL: Email availability for user updates
     * Complex business logic, keep as optimized custom query
     */
    @Query("SELECT CASE WHEN COUNT(u) = 0 THEN true ELSE false END FROM User u " +
            "WHERE LOWER(u.email) = LOWER(:email) AND (:userId IS NULL OR u.id != :userId)")
    boolean isEmailAvailable(@Param("email") String email, @Param("userId") Long userId);

    /**
     * ⚡ PERFORMANCE: Simple count operations
     * Frequently used, keep as custom for speed
     */
    long countByRole(UserRole role);

    long countByStatus(UserStatus status);

    /**
     * ⚡ PERFORMANCE: Active users count (dashboard)
     * Very common query, optimize with custom
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();

    /**
     * 🛠️ BULK OPERATIONS: Mass status updates
     * Performance critical bulk operations
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :newStatus, u.lastModified = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int updateStatusForUsers(@Param("userIds") List<Long> userIds, @Param("newStatus") UserStatus newStatus);

    @Modifying
    @Query("UPDATE User u SET u.status = 'ACTIVE', u.lastModified = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int activateUsers(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query("UPDATE User u SET u.status = 'INACTIVE', u.lastModified = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int deactivateUsers(@Param("userIds") List<Long> userIds);

    /**
     * 📊 STATISTICS: Timezone distribution
     * Simple aggregation, keep as custom
     */
    @Query("SELECT DISTINCT u.userTimezone FROM User u WHERE u.userTimezone IS NOT NULL ORDER BY u.userTimezone")
    List<String> findDistinctTimezones();

    @Query("SELECT u.userTimezone, COUNT(u) FROM User u WHERE u.userTimezone IS NOT NULL GROUP BY u.userTimezone ORDER BY COUNT(u) DESC")
    List<Object[]> getTimezoneDistribution();

    /**
     * 📊 STATISTICS: Role and status distributions
     * Simple aggregations for reports
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role ORDER BY COUNT(u) DESC")
    List<Object[]> getRoleDistribution();

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status ORDER BY COUNT(u) DESC")
    List<Object[]> getStatusDistribution();

    // ===== DYNAMIC QUERY METHODS (Flexibility & Business Logic) =====

    /**
     * 🚀 MAIN SEARCH METHOD: Multi-criteria user search
     *
     * This method handles complex searches with optional parameters.
     * Uses dynamic specifications for maximum flexibility.
     *
     * Examples:
     * - Find active admins: hasRole(ADMIN).and(isActive())
     * - Search by name: hasNameOrEmailContaining("john")
     * - Complex filters: buildSearchSpecification(term, role, status, timezone, dates)
     *
     * Inherited from BaseRepository -> JpaSpecificationExecutor:
     * - Page<User> findAll(Specification<User> spec, Pageable pageable)
     * - List<User> findAll(Specification<User> spec)
     * - long count(Specification<User> spec)
     */

    // ===== HYBRID CONVENIENCE METHODS =====

    /**
     * 🔍 SEARCH: Text search across name and email
     * Uses dynamic specification for flexible text searching
     */
    default Page<User> searchByNameOrEmail(String searchTerm, Pageable pageable) {
        return findAll(UserSpecifications.hasNameOrEmailContaining(searchTerm), pageable);
    }

    /**
     * 🔍 SEARCH: Advanced multi-criteria search
     * Combines multiple specifications dynamically
     */
    default Page<User> advancedSearch(String searchTerm, UserRole role, UserStatus status,
                                      String timezone, ZonedDateTime createdAfter,
                                      ZonedDateTime createdBefore, Pageable pageable) {
        return findAll(UserSpecifications.buildSearchSpecification(
                searchTerm, role, status, timezone, createdAfter, createdBefore), pageable);
    }

    /**
     * 👥 BUSINESS LOGIC: Find users by role
     * Simple specification for common use case
     */
    default Page<User> findByRole(UserRole role, Pageable pageable) {
        return findAll(UserSpecifications.hasRole(role), pageable);
    }

    /**
     * 👥 BUSINESS LOGIC: Find active users
     * Uses specification for consistency with business rules
     */
    default Page<User> findActiveUsers(Pageable pageable) {
        return findAll(UserSpecifications.isActive(), pageable);
    }

    /**
     * 👥 BUSINESS LOGIC: Find administrative users
     * Complex business logic in specification
     */
    default List<User> findActiveAdministrativeUsers() {
        return findAll(UserSpecifications.canManageUsers());
    }

    /**
     * 👥 BUSINESS LOGIC: Find users requiring attention
     * Business rule encapsulated in specification
     */
    default Page<User> findUsersRequiringAttention(Pageable pageable) {
        return findAll(UserSpecifications.requiresAttention(), pageable);
    }

    /**
     * 📅 DATE FILTERING: Users created recently
     * Dynamic date filtering with business logic
     */
    default Page<User> findRecentUsers(int days, Pageable pageable) {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(days);
        return findAll(UserSpecifications.createdAfter(cutoffDate), pageable);
    }

    /**
     * 🌍 TIMEZONE: Users in specific timezone
     * Geographic filtering with specification
     */
    default Page<User> findUsersByTimezone(String timezone, Pageable pageable) {
        return findAll(UserSpecifications.hasTimezone(timezone), pageable);
    }

    /**
     * 🔢 COUNTING: Dynamic count operations
     * Use specifications for consistent business logic
     */
    default long countAdministrativeUsers() {
        return count(UserSpecifications.isAdministrative());
    }

    default long countUsersRequiringAttention() {
        return count(UserSpecifications.requiresAttention());
    }

    default long countUsersByTimezone(String timezone) {
        return count(UserSpecifications.hasTimezone(timezone));
    }

    // ===== EXAMPLES OF WHEN TO USE EACH APPROACH =====

    /*
     * 🔥 USE CUSTOM @Query FOR:
     *
     * 1. AUTHENTICATION/SECURITY:
     *    findByEmail() - Login performance critical
     *    existsByEmailIgnoreCase() - Registration validation
     *    isEmailAvailable() - Update validation
     *
     * 2. SIMPLE, FREQUENT OPERATIONS:
     *    countByRole() - Dashboard metrics
     *    countActiveUsers() - System statistics
     *    findDistinctTimezones() - Dropdown population
     *
     * 3. BULK OPERATIONS:
     *    updateStatusForUsers() - Mass user management
     *    activateUsers() - Batch operations
     *    deactivateUsers() - Administrative actions
     *
     * 4. REPORTING AGGREGATIONS:
     *    getRoleDistribution() - System reports
     *    getTimezoneDistribution() - Analytics
     *
     * 🚀 USE DYNAMIC SPECIFICATIONS FOR:
     *
     * 1. SEARCH FUNCTIONALITY:
     *    searchByNameOrEmail() - User search box
     *    advancedSearch() - Multi-filter search
     *
     * 2. BUSINESS LOGIC:
     *    findActiveAdministrativeUsers() - Complex rules
     *    findUsersRequiringAttention() - Status logic
     *
     * 3. OPTIONAL PARAMETERS:
     *    All search methods with optional filters
     *    API endpoints with query parameters
     *
     * 4. COMPLEX COMBINATIONS:
     *    Multiple criteria that change based on context
     *    Report filters that vary by user type
     */
}