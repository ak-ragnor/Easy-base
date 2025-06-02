package com.easyBase.web.controller.api.v1;

import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.dto.user.CreateUserRequest;
import com.easyBase.common.dto.user.UpdateUserRequest;
import com.easyBase.common.service.TimezoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for User Management with full timezone support
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private TimezoneService timezoneService;

    // Mock data store - in real app this would be a service layer
    private static final Map<Long, UserDTO> USERS = new HashMap<>();
    private static Long USER_ID_COUNTER = 1L;

    // Initialize with sample data using different timezones
    @PostConstruct
    public void init() {
        ZonedDateTime now = timezoneService.now();

        USERS.put(1L, UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .role("ADMIN")
                .status("ACTIVE")
                .createdAt(now)
                .lastModified(now)
                .userTimezone("America/New_York")
                .build());

        USERS.put(2L, UserDTO.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .role("USER")
                .status("ACTIVE")
                .createdAt(now)
                .lastModified(now)
                .userTimezone("Europe/London")
                .build());

        USERS.put(3L, UserDTO.builder()
                .id(3L)
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .role("MANAGER")
                .status("ACTIVE")
                .createdAt(now)
                .lastModified(now)
                .userTimezone("Asia/Tokyo")
                .build());

        USERS.put(4L, UserDTO.builder()
                .id(4L)
                .name("Alice Brown")
                .email("alice.brown@example.com")
                .role("USER")
                .status("INACTIVE")
                .createdAt(now)
                .lastModified(now)
                .userTimezone("UTC")
                .build());

        USER_ID_COUNTER = 5L;
    }

    /**
     * Get all users with optional filtering and pagination
     * Optionally convert to requester's timezone
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "role", defaultValue = "") String role,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        System.out.println("=== UserController.getAllUsers() called ===");
        System.out.println("User timezone header: " + userTimezone);

        try {
            List<UserDTO> filteredUsers = new ArrayList<>(USERS.values());

            // Apply filters
            if (!search.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                filteredUsers = filteredUsers.stream()
                        .filter(user -> (user.getName() != null && user.getName().toLowerCase().contains(search.toLowerCase())) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(search.toLowerCase())))
                        .collect(Collectors.toList());

            if (!role.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getRole().equalsIgnoreCase(role))
                        .collect(Collectors.toList());
            }

            if (!status.isEmpty()) {
                filteredUsers = filteredUsers.stream()
                        .filter(user -> user.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }

            // Convert to user's timezone if provided
            if (userTimezone != null && timezoneService.isValidTimezone(userTimezone) && timezoneService.isUserTimezoneEnabled()) {
                filteredUsers = filteredUsers.stream()
                        .map(user -> convertToUserTimezone(user, userTimezone))
                        .collect(Collectors.toList());
            }

            // Apply pagination
            int totalElements = filteredUsers.size();
            int totalPages = (int) Math.ceil((double) totalElements / Math.max(1, size));
            int startIndex = Math.max(0, page) * Math.max(1, size);
            int endIndex = Math.min(startIndex + Math.max(1, size), totalElements);

            List<UserDTO> pageContent = startIndex < totalElements ?
                    filteredUsers.subList(startIndex, endIndex) : new ArrayList<>();

            Map<String, Object> response = new HashMap<>();
            response.put("content", pageContent);
            response.put("page", page);
            response.put("size", size);
            response.put("totalElements", totalElements);
            response.put("totalPages", totalPages);
            response.put("first", page == 0);
            response.put("last", page >= totalPages - 1);
            response.put("serverTimezone", timezoneService.getSystemTimezone());
            response.put("timestamp", timezoneService.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR in getAllUsers: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable("id") Long id,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        System.out.println("=== UserController.getUserById() called with ID: " + id + " ===");

        UserDTO user = USERS.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Convert to user's timezone if provided
        if (userTimezone != null && timezoneService.isValidTimezone(userTimezone)) {
            user = convertToUserTimezone(user, userTimezone);
        }

        return ResponseEntity.ok(user);
    }

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestBody CreateUserRequest request,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        System.out.println("=== UserController.createUser() called ===");

        // Validate email uniqueness
        boolean emailExists = USERS.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(request.getEmail()));

        if (emailExists) {
            return ResponseEntity.badRequest().build();
        }

        // Create new user with proper timezone
        Long newId = USER_ID_COUNTER++;
        ZonedDateTime now = timezoneService.now();

        UserDTO newUser = UserDTO.builder()
                .id(newId)
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .status("ACTIVE")
                .createdAt(now)
                .lastModified(now)
                .userTimezone(userTimezone != null ? userTimezone : timezoneService.getSystemTimezone())
                .build();

        USERS.put(newId, newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserRequest request,
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {

        System.out.println("=== UserController.updateUser() called with ID: " + id + " ===");

        UserDTO existingUser = USERS.get(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Check email uniqueness (excluding current user)
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            boolean emailExists = USERS.values().stream()
                    .anyMatch(user -> !user.getId().equals(id) &&
                            user.getEmail().equalsIgnoreCase(request.getEmail()));

            if (emailExists) {
                return ResponseEntity.badRequest().build();
            }
        }

        // Update user fields
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(request.getStatus());
        }
        if (request.getTimezone() != null && timezoneService.isValidTimezone(request.getTimezone())) {
            existingUser.setUserTimezone(request.getTimezone());
        }

        existingUser.setLastModified(timezoneService.now());

        return ResponseEntity.ok(existingUser);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        System.out.println("=== UserController.deleteUser() called with ID: " + id + " ===");

        UserDTO user = USERS.remove(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(
            @RequestHeader(name = "X-User-Timezone", required = false) String userTimezone) {
        if (userTimezone != null && !timezoneService.isValidTimezone(userTimezone)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid timezone"));
        }

        long totalUsers = USERS.size();
        long activeUsers = USERS.values().stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
        long inactiveUsers = totalUsers - activeUsers;

        Map<String, Long> roleStats = new HashMap<>();
        USERS.values().forEach(user ->
                roleStats.merge(user.getRole(), 1L, Long::sum)
        );

        // Timezone distribution
        Map<String, Long> timezoneStats = new HashMap<>();
        USERS.values().forEach(user ->
                timezoneStats.merge(user.getUserTimezone(), 1L, Long::sum)
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("roleDistribution", roleStats);
        stats.put("timezoneDistribution", timezoneStats);
        stats.put("serverTimezone", timezoneService.getSystemTimezone());
        stats.put("lastUpdated", timezoneService.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get available timezones
     */
    @GetMapping("/timezones")
    public ResponseEntity<Map<String, Object>> getAvailableTimezones() {
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

        Map<String, Object> response = new HashMap<>();
        response.put("timezones", timezones);
        response.put("default", timezoneService.getSystemTimezone());

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to convert user dates to requested timezone
     */
    private UserDTO convertToUserTimezone(UserDTO user, String targetTimezone) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(timezoneService.convertTimezone(user.getCreatedAt(), targetTimezone))
                .lastModified(timezoneService.convertTimezone(user.getLastModified(), targetTimezone))
                .userTimezone(user.getUserTimezone())
                .build();
    }
}