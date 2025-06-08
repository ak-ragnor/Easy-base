package com.easyBase.service.business;

import com.easyBase.common.dto.user.CreateUserRequest;
import com.easyBase.common.dto.user.UpdateUserRequest;
import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.dto.user.UserSearchCriteria;
import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Enterprise User Service Interface
 *
 * Defines comprehensive user management operations following enterprise standards:
 * - Service layer abstraction for business logic
 * - Validation support with JSR-303 annotations
 * - Consistent return types and error handling
 * - Transaction boundary definitions
 * - Clear separation of concerns
 *
 * This interface serves as the contract between the web layer and business logic,
 * ensuring proper abstraction and testability.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public interface UserService {

    // ===== CORE CRUD OPERATIONS =====

    /**
     * Find user by unique identifier
     *
     * @param id the user ID
     * @return UserDTO with complete user information
     * @throws ResourceNotFoundException if user not found
     */
    UserDTO findById(@NotNull Long id);

    /**
     * Find user by email address
     *
     * @param email the user's email
     * @return UserDTO with complete user information
     * @throws ResourceNotFoundException if user not found
     */
    UserDTO findByEmail(@NotNull String email);

    /**
     * Find all users with pagination support
     *
     * @param pageable pagination parameters
     * @return Page of UserDTOs
     */
    Page<UserDTO> findAll(Pageable pageable);

    /**
     * Create a new user
     *
     * @param request validated user creation request
     * @return created UserDTO with generated ID and audit fields
     * @throws BusinessException if email already exists or validation fails
     */
    UserDTO create(@Valid CreateUserRequest request);

    /**
     * Update an existing user
     *
     * @param id the user ID to update
     * @param request validated user update request
     * @return updated UserDTO
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessException if email conflict or validation fails
     */
    UserDTO update(@NotNull Long id, @Valid UpdateUserRequest request);

    /**
     * Delete a user by ID
     *
     * @param id the user ID to delete
     * @throws ResourceNotFoundException if user not found
     * @throws BusinessException if user cannot be deleted (e.g., last admin)
     */
    void delete(@NotNull Long id);

    // ===== SEARCH AND FILTERING =====

    /**
     * Advanced search with multiple criteria
     *
     * @param criteria search criteria object
     * @param pageable pagination parameters
     * @return Page of matching UserDTOs
     */
    Page<UserDTO> search(UserSearchCriteria criteria, Pageable pageable);

    /**
     * Find users by role
     *
     * @param role the user role to filter by
     * @param pageable pagination parameters
     * @return Page of UserDTOs with specified role
     */
    Page<UserDTO> findByRole(UserRole role, Pageable pageable);

    /**
     * Find users by status
     *
     * @param status the user status to filter by
     * @param pageable pagination parameters
     * @return Page of UserDTOs with specified status
     */
    Page<UserDTO> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find all active users
     *
     * @param pageable pagination parameters
     * @return Page of active UserDTOs
     */
    Page<UserDTO> findActiveUsers(Pageable pageable);

    /**
     * Find users created within the last N days
     *
     * @param days number of days to look back
     * @param pageable pagination parameters
     * @return Page of recently created UserDTOs
     */
    Page<UserDTO> findRecentUsers(int days, Pageable pageable);

    /**
     * Find users in a specific timezone
     *
     * @param timezone the timezone to filter by
     * @param pageable pagination parameters
     * @return Page of UserDTOs in specified timezone
     */
    Page<UserDTO> findUsersByTimezone(String timezone, Pageable pageable);

    // ===== BULK OPERATIONS =====

    /**
     * Update status for multiple users
     *
     * @param userIds list of user IDs to update
     * @param newStatus the new status to set
     * @return number of users updated
     * @throws BusinessException if operation violates business rules
     */
    int updateUserStatus(List<Long> userIds, UserStatus newStatus);

    /**
     * Activate multiple users
     *
     * @param userIds list of user IDs to activate
     * @return number of users activated
     */
    int activateUsers(List<Long> userIds);

    /**
     * Deactivate multiple users
     *
     * @param userIds list of user IDs to deactivate
     * @return number of users deactivated
     * @throws BusinessException if attempting to deactivate all admins
     */
    int deactivateUsers(List<Long> userIds);

    // ===== STATISTICS AND ANALYTICS =====

    /**
     * Get comprehensive user statistics
     *
     * @return Map containing various user metrics and distributions
     */
    Map<String, Object> getUserStatistics();

    /**
     * Get list of all timezones currently used by users
     *
     * @return List of distinct timezone strings
     */
    List<String> getAvailableTimezones();

    /**
     * Get count of users by role
     *
     * @param role the role to count
     * @return number of users with specified role
     */
    long countUsersByRole(UserRole role);

    /**
     * Get count of users by status
     *
     * @param status the status to count
     * @return number of users with specified status
     */
    long countUsersByStatus(UserStatus status);

    // ===== TIMEZONE OPERATIONS =====

    /**
     * Convert user's timestamps to a specific timezone
     *
     * @param userId the user ID
     * @param targetTimezone the target timezone
     * @return UserDTO with converted timestamps
     * @throws BusinessException if timezone is invalid
     */
    UserDTO convertToUserTimezone(Long userId, String targetTimezone);

    // ===== BUSINESS VALIDATION =====

    /**
     * Check if an email address is available for use
     *
     * @param email the email to check
     * @param excludeUserId user ID to exclude from check (for updates)
     * @return true if email is available
     */
    boolean isEmailAvailable(String email, Long excludeUserId);

    /**
     * Check if a user can be safely deleted
     *
     * @param userId the user ID to check
     * @return true if user can be deleted
     */
    boolean canUserBeDeleted(Long userId);
}