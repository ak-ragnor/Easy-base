package com.easyBase.web.controller.api.v1;

import com.easyBase.common.dto.user.CreateUserRequest;
import com.easyBase.common.dto.user.UpdateUserRequest;
import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.service.TimezoneService;
import com.easyBase.service.business.UserService;
import com.easyBase.web.controller.base.BaseController;
import com.easyBase.web.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User REST Controller Implementation
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TimezoneService timezoneService;

    /**
     * Get all users with optional filtering and pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "role", defaultValue = "") String role,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        logger.info("GET /api/v1/users - page: {}, size: {}, search: '{}', role: {}, status: {}",
                page, size, search, role, status);

        try {
            int validatedSize = Math.min(Math.max(size, 1), 100); // Between 1 and 100
            int validatedPage = Math.max(page, 0); // Non-negative

            Pageable pageable = PageRequest.of(validatedPage, validatedSize, Sort.by("name"));

            Page<UserDTO> users = userService.findAll(pageable);

            List<UserDTO> filteredUsers = users.getContent();

            if (!search.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                        .filter(user ->
                                (user.getName() != null && user.getName().toLowerCase().contains(search.toLowerCase())) ||
                                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(search.toLowerCase())))
                        .collect(Collectors.toList());
            }

            if (!role.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getRole() != null && user.getRole().equalsIgnoreCase(role))
                        .collect(Collectors.toList());
            }

            if (!status.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getStatus() != null && user.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }

            if (userTimezone != null && timezoneService.isValidTimezone(userTimezone) &&
                    timezoneService.isUserTimezoneEnabled()) {
                filteredUsers = filteredUsers.stream()
                        .map(user -> _convertToUserTimezone(user, userTimezone))
                        .collect(Collectors.toList());
            }

            long totalElements = filteredUsers.size();
            int totalPages = (int) Math.ceil((double) totalElements / validatedSize);
            int startIndex = validatedPage * validatedSize;
            int endIndex = Math.min(startIndex + validatedSize, (int) totalElements);

            List<UserDTO> pageContent = startIndex < totalElements ?
                    filteredUsers.subList(startIndex, endIndex) : new ArrayList<>();

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageContent);
            response.put("page", validatedPage);
            response.put("size", validatedSize);
            response.put("totalElements", totalElements);
            response.put("totalPages", totalPages);
            response.put("first", validatedPage == 0);
            response.put("last", validatedPage >= totalPages - 1);
            response.put("serverTimezone", timezoneService.getSystemTimezone());
            response.put("timestamp", timezoneService.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("ERROR in getAllUsers: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to retrieve users: " + e.getMessage());
            errorResponse.put("timestamp", timezoneService.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable("id") Long id,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        logger.info("GET /api/v1/users/{} - Retrieving user by ID", id);

        try {
            UserDTO user = userService.findById(id);

            if (userTimezone != null && timezoneService.isValidTimezone(userTimezone)) {
                user = _convertToUserTimezone(user, userTimezone);
            }

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(true)
                    .data(user)
                    .message("User retrieved successfully")
                    .metadata(Map.of(
                            "userId", id,
                            "serverTimezone", timezoneService.getSystemTimezone(),
                            "clientTimezone", userTimezone != null ? userTimezone : "Not specified",
                            "timestamp", timezoneService.now()
                    ))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving user {}: {}", id, e.getMessage(), e);

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(false)
                    .message("Failed to retrieve user: " + e.getMessage())
                    .errorCode("USER_RETRIEVAL_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        logger.info("POST /api/v1/users - Creating user with email: {}", request.getEmail());

        try {
            UserDTO createdUser = userService.create(request);

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(true)
                    .data(createdUser)
                    .message("User created successfully")
                    .metadata(Map.of(
                            "userId", createdUser.getId(),
                            "email", createdUser.getEmail(),
                            "role", createdUser.getRole(),
                            "timestamp", timezoneService.now()
                    ))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(false)
                    .message("Failed to create user: " + e.getMessage())
                    .errorCode("USER_CREATION_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        logger.info("PUT /api/v1/users/{} - Updating user", id);

        try {
            UserDTO updatedUser = userService.update(id, request);

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(true)
                    .data(updatedUser)
                    .message("User updated successfully")
                    .metadata(Map.of(
                            "userId", id,
                            "email", updatedUser.getEmail(),
                            "lastModified", updatedUser.getLastModified(),
                            "timestamp", timezoneService.now()
                    ))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating user {}: {}", id, e.getMessage(), e);

            ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                    .success(false)
                    .message("Failed to update user: " + e.getMessage())
                    .errorCode("USER_UPDATE_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        logger.info("DELETE /api/v1/users/{} - Deleting user", id);

        try {
            userService.delete(id);

            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("User deleted successfully")
                    .metadata(Map.of(
                            "deletedUserId", id,
                            "timestamp", timezoneService.now()
                    ))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage(), e);

            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete user: " + e.getMessage())
                    .errorCode("USER_DELETION_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ===== STATISTICS AND UTILITY ENDPOINTS =====

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats(
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        logger.info("GET /api/v1/users/stats - Retrieving user statistics");

        try {
            Map<String, Object> stats = userService.getUserStatistics();

            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .data(stats)
                    .message("User statistics retrieved successfully")
                    .metadata(Map.of(
                            "serverTimezone", timezoneService.getSystemTimezone(),
                            "timestamp", timezoneService.now()
                    ))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving user statistics: {}", e.getMessage(), e);

            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to retrieve statistics: " + e.getMessage())
                    .errorCode("STATS_RETRIEVAL_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get available timezones
     */
    @GetMapping("/timezones")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableTimezones() {
        logger.info("GET /api/v1/users/timezones - Retrieving available timezones");

        try {
            List<String> timezones = Arrays.asList(
                    "UTC",
                    "America/New_York",
                    "America/Chicago",
                    "America/Denver",
                    "America/Los_Angeles",
                    "Europe/London",
                    "Europe/Paris",
                    "Europe/Berlin",
                    "Asia/Tokyo",
                    "Asia/Shanghai",
                    "Asia/Kolkata",
                    "Australia/Sydney",
                    "Pacific/Auckland"
            );

            Map<String, Object> response_data = new HashMap<>();
            response_data.put("timezones", timezones);
            response_data.put("default", timezoneService.getSystemTimezone());
            response_data.put("count", timezones.size());

            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .data(response_data)
                    .message("Available timezones retrieved successfully")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving timezones: {}", e.getMessage(), e);

            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to retrieve timezones: " + e.getMessage())
                    .errorCode("TIMEZONE_RETRIEVAL_FAILED")
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper method to convert user dates to requested timezone
     */
    private UserDTO _convertToUserTimezone(UserDTO user, String targetTimezone) {
        if (user == null || targetTimezone == null) {
            return user;
        }

        try {
            UserDTO convertedUser = UserDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .userTimezone(user.getUserTimezone())
                    .build();

            if (user.getCreatedAt() != null) {
                convertedUser.setCreatedAt(timezoneService.convertTimezone(user.getCreatedAt(), targetTimezone));
            }
            if (user.getLastModified() != null) {
                convertedUser.setLastModified(timezoneService.convertTimezone(user.getLastModified(), targetTimezone));
            }

            return convertedUser;

        } catch (Exception e) {
            logger.warn("Failed to convert user {} to timezone {}: {}", user.getId(), targetTimezone, e.getMessage());
            return user; // Return original if conversion fails
        }
    }
}