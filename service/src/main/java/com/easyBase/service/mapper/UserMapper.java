package com.easyBase.service.mapper;

import com.easyBase.common.dto.user.CreateUserRequest;
import com.easyBase.common.dto.user.UpdateUserRequest;
import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise User Mapper
 *
 * Handles mapping between User entities and DTOs with:
 * - Null-safe conversions
 * - Proper enum handling
 * - Selective field updates
 * - Enterprise validation
 * - Performance optimization
 *
 * This mapper follows enterprise patterns for data transformation
 * and ensures consistent mapping logic across the application.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class UserMapper {

    // ===== ENTITY TO DTO CONVERSIONS =====

    /**
     * Convert User entity to UserDTO
     *
     * @param user the user entity
     * @return UserDTO or null if input is null
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .createdAt(user.getCreatedAt())
                .lastModified(user.getLastModified())
                .userTimezone(user.getUserTimezone())
                .build();
    }

    /**
     * Convert list of User entities to list of UserDTOs
     *
     * @param users list of user entities
     * @return list of UserDTOs, empty list if input is null
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== DTO TO ENTITY CONVERSIONS =====

    /**
     * Convert CreateUserRequest to User entity
     *
     * @param request the creation request
     * @return new User entity or null if input is null
     */
    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Handle role conversion with validation
        if (request.getRole() != null) {
            user.setRole(parseUserRole(request.getRole()));
        } else {
            user.setRole(UserRole.USER); // Default role
        }

        user.setStatus(UserStatus.ACTIVE); // Default status for new users

        // Handle timezone
        if (request.getTimezone() != null && !request.getTimezone().trim().isEmpty()) {
            user.setUserTimezone(request.getTimezone());
        } else {
            user.setUserTimezone("UTC"); // Default timezone
        }

        return user;
    }

    /**
     * Update existing User entity with UpdateUserRequest data
     * Only updates non-null fields from the request
     *
     * @param user the existing user entity to update
     * @param request the update request
     */
    public void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail().trim().toLowerCase());
        }

        // Update role if provided
        if (request.getRole() != null) {
            user.setRole(parseUserRole(request.getRole()));
        }

        // Update status if provided
        if (request.getStatus() != null) {
            user.setStatus(parseUserStatus(request.getStatus()));
        }

        // Update timezone if provided
        if (request.getTimezone() != null && !request.getTimezone().trim().isEmpty()) {
            user.setUserTimezone(request.getTimezone().trim());
        }
    }

    // ===== CONVERSION UTILITIES =====

    /**
     * Parse string to UserRole enum safely
     *
     * @param roleString the role string
     * @return UserRole enum
     * @throws IllegalArgumentException if role is invalid
     */
    private UserRole parseUserRole(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return UserRole.USER; // Default role
        }

        try {
            return UserRole.valueOf(roleString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + roleString +
                    ". Valid roles are: ADMIN, MANAGER, USER");
        }
    }

    /**
     * Parse string to UserStatus enum safely
     *
     * @param statusString the status string
     * @return UserStatus enum
     * @throws IllegalArgumentException if status is invalid
     */
    private UserStatus parseUserStatus(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            return UserStatus.ACTIVE; // Default status
        }

        try {
            return UserStatus.valueOf(statusString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user status: " + statusString +
                    ". Valid statuses are: ACTIVE, INACTIVE, LOCKED, SUSPENDED, DISABLED");
        }
    }

    // ===== VALIDATION UTILITIES =====

    /**
     * Validate that required fields are present in CreateUserRequest
     *
     * @param request the creation request
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create user request cannot be null");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }

        // Validate email format (basic check)
        String email = request.getEmail().trim();
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate role if provided
        if (request.getRole() != null) {
            parseUserRole(request.getRole()); // Will throw exception if invalid
        }
    }

    /**
     * Validate that at least one field is provided in UpdateUserRequest
     *
     * @param request the update request
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update user request cannot be null");
        }

        boolean hasUpdate = request.getName() != null ||
                request.getEmail() != null ||
                request.getRole() != null ||
                request.getStatus() != null ||
                request.getTimezone() != null;

        if (!hasUpdate) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        // Validate email format if provided
        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }

        // Validate role if provided
        if (request.getRole() != null) {
            parseUserRole(request.getRole()); // Will throw exception if invalid
        }

        // Validate status if provided
        if (request.getStatus() != null) {
            parseUserStatus(request.getStatus()); // Will throw exception if invalid
        }
    }

    /**
     * Create a summary DTO with limited information
     * Useful for listings and dropdown selections
     *
     * @param user the user entity
     * @return UserDTO with only essential fields
     */
    public UserDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }

    /**
     * Convert list of User entities to summary DTOs
     *
     * @param users list of user entities
     * @return list of summary UserDTOs
     */
    public List<UserDTO> toSummaryDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Copy user entity (for audit or backup purposes)
     * Creates a detached copy without ID and audit fields
     *
     * @param source the source user entity
     * @return new User entity copy
     */
    public User copyEntity(User source) {
        if (source == null) {
            return null;
        }

        User copy = new User();
        copy.setName(source.getName());
        copy.setEmail(source.getEmail());
        copy.setRole(source.getRole());
        copy.setStatus(source.getStatus());
        copy.setUserTimezone(source.getUserTimezone());

        return copy;
    }
}