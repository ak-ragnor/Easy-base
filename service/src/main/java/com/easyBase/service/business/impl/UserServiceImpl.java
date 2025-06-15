package com.easyBase.service.business.impl;

import com.easyBase.common.dto.user.CreateUserRequest;
import com.easyBase.common.dto.user.UpdateUserRequest;
import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.dto.user.UserSearchCriteria;
import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.common.service.TimezoneService;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.user.UserRepository;
import com.easyBase.domain.specification.UserSpecifications;
import com.easyBase.service.business.UserService;
import com.easyBase.service.exception.BusinessException;
import com.easyBase.service.exception.ResourceNotFoundException;
import com.easyBase.service.mapper.UserMapper;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enterprise User Service Implementation
 *
 * Provides comprehensive user management operations with:
 * - Full CRUD operations with proper validation
 * - Advanced search and filtering capabilities
 * - Timezone-aware operations
 * - Business rule enforcement
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
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TimezoneService timezoneService;

    // ===== CORE CRUD OPERATIONS =====

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(@NotNull Long id) {
        logger.debug("Finding user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByEmail(@NotNull String email) {
        logger.debug("Finding user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        logger.debug("Finding all users with pagination: {}", pageable);

        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDTO);
    }

    @Override
    public UserDTO create(@Valid CreateUserRequest request) {
        logger.info("Creating new user with email: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessException("User already exists with email: " + request.getEmail());
        }

        // Create user entity
        User user = userMapper.toEntity(request);

        // Set defaults if not provided
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        if (user.getUserTimezone() == null) {
            user.setUserTimezone(timezoneService.getSystemTimezone());
        }

        // Validate timezone
        if (!timezoneService.isValidTimezone(user.getUserTimezone())) {
            logger.warn("Invalid timezone provided: {}, using system default", user.getUserTimezone());
            user.setUserTimezone(timezoneService.getSystemTimezone());
        }

        // Save user
        User savedUser = userRepository.save(user);

        logger.info("User created successfully with ID: {} and email: {}",
                savedUser.getId(), savedUser.getEmail());

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO update(@NotNull Long id, @Valid UpdateUserRequest request) {
        logger.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Validate email uniqueness (if email is being changed)
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (!userRepository.isEmailAvailable(request.getEmail(), id)) {
                throw new BusinessException("Email already in use: " + request.getEmail());
            }
        }

        // Update fields selectively
        userMapper.updateEntity(existingUser, request);

        // Validate timezone if provided
        if (request.getTimezone() != null && !timezoneService.isValidTimezone(request.getTimezone())) {
            throw new BusinessException("Invalid timezone: " + request.getTimezone());
        }

        User updatedUser = userRepository.save(existingUser);

        logger.info("User updated successfully with ID: {}", updatedUser.getId());

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void delete(@NotNull Long id) {
        logger.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Business rule: Cannot delete the last admin
        if (user.getRole() == UserRole.ADMIN) {
            long adminCount = userRepository.countByRole(UserRole.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessException("Cannot delete the last administrator");
            }
        }

        userRepository.delete(user);

        logger.info("User deleted successfully with ID: {}", id);
    }

    // ===== SEARCH AND FILTERING =====

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> search(UserSearchCriteria criteria, Pageable pageable) {
        logger.debug("Searching users with criteria: {}", criteria);

        Specification<User> spec = UserSpecifications.buildSearchSpecification(
                criteria.getSearchTerm(),
                criteria.getRole(),
                criteria.getStatus(),
                criteria.getTimezone(),
                criteria.getCreatedAfter(),
                criteria.getCreatedBefore()
        );

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByRole(UserRole role, Pageable pageable) {
        logger.debug("Finding users by role: {}", role);

        Page<User> users = userRepository.findByRole(role, pageable);

        return users.map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByStatus(UserStatus status, Pageable pageable) {
        logger.debug("Finding users by status: {}", status);

        Specification<User> spec = UserSpecifications.hasStatus(status);
        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findActiveUsers(Pageable pageable) {
        logger.debug("Finding active users");

        Page<User> users = userRepository.findActiveUsers(pageable);

        return users.map(userMapper::toDTO);
    }

    // ===== BULK OPERATIONS =====

    @Override
    public int updateUserStatus(List<Long> userIds, UserStatus newStatus) {
        logger.info("Bulk updating status to {} for {} users", newStatus, userIds.size());

        // Validate that users exist
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new BusinessException("Some users not found in the provided IDs");
        }

        // Business rule: Cannot deactivate all admins
        if (newStatus != UserStatus.ACTIVE) {
            long adminCount = users.stream()
                    .filter(user -> user.getRole() == UserRole.ADMIN)
                    .count();

            long totalAdmins = userRepository.countByRole(UserRole.ADMIN);

            if (adminCount >= totalAdmins) {
                throw new BusinessException("Cannot deactivate all administrators");
            }
        }

        int updatedCount = userRepository.updateStatusForUsers(userIds, newStatus);

        logger.info("Successfully updated status for {} users", updatedCount);

        return updatedCount;
    }

    @Override
    public int activateUsers(List<Long> userIds) {
        logger.info("Bulk activating {} users", userIds.size());
        return userRepository.activateUsers(userIds);
    }

    @Override
    public int deactivateUsers(List<Long> userIds) {
        logger.info("Bulk deactivating {} users", userIds.size());

        // Validate admin constraint
        List<User> users = userRepository.findAllById(userIds);
        long adminCount = users.stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .count();

        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);

        if (adminCount >= totalAdmins) {
            throw new BusinessException("Cannot deactivate all administrators");
        }

        return userRepository.deactivateUsers(userIds);
    }

    // ===== STATISTICS AND ANALYTICS =====

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics() {
        logger.debug("Generating user statistics");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long inactiveUsers = totalUsers - activeUsers;

        // Role distribution
        Map<UserRole, Long> roleDistribution = userRepository.getRoleDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (UserRole) row[0],
                        row -> (Long) row[1]
                ));

        // Status distribution
        Map<UserStatus, Long> statusDistribution = userRepository.getStatusDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (UserStatus) row[0],
                        row -> (Long) row[1]
                ));

        // Timezone distribution
        Map<String, Long> timezoneDistribution = userRepository.getTimezoneDistribution()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "inactiveUsers", inactiveUsers,
                "roleDistribution", roleDistribution,
                "statusDistribution", statusDistribution,
                "timezoneDistribution", timezoneDistribution,
                "lastUpdated", timezoneService.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableTimezones() {
        return userRepository.findDistinctTimezones();
    }

    // ===== TIMEZONE OPERATIONS =====

    @Override
    @Transactional(readOnly = true)
    public UserDTO convertToUserTimezone(Long userId, String targetTimezone) {
        logger.debug("Converting user {} data to timezone: {}", userId, targetTimezone);

        if (!timezoneService.isValidTimezone(targetTimezone)) {
            throw new BusinessException("Invalid timezone: " + targetTimezone);
        }

        UserDTO user = findById(userId);

        // Convert timestamps to target timezone
        if (user.getCreatedAt() != null) {
            user.setCreatedAt(timezoneService.convertTimezone(user.getCreatedAt(), targetTimezone));
        }
        if (user.getLastModified() != null) {
            user.setLastModified(timezoneService.convertTimezone(user.getLastModified(), targetTimezone));
        }

        return user;
    }

    // ===== BUSINESS VALIDATION =====

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        return userRepository.isEmailAvailable(email, excludeUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserBeDeleted(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // Cannot delete last admin
        if (user.getRole() == UserRole.ADMIN) {
            long adminCount = userRepository.countByRole(UserRole.ADMIN);
            return adminCount > 1;
        }

        return true;
    }

    // ===== HELPER METHODS =====

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findRecentUsers(int days, Pageable pageable) {

        Page<User> users = userRepository.findRecentUsers(days, pageable);

        return users.map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findUsersByTimezone(String timezone, Pageable pageable) {

        Page<User> users = userRepository.findUsersByTimezone(timezone, pageable);

        return users.map(userMapper::toDTO);
    }
}