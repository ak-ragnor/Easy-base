package com.easyBase.web.controller.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for User Management
 * Provides full CRUD operations for user entities
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // Mock data store - in real app this would be a service layer
    private static final Map<Long, User> USERS = new HashMap<>();
    private static Long USER_ID_COUNTER = 1L;

    static {
        // Initialize with sample data
        USERS.put(1L, new User(1L, "John Doe", "john.doe@example.com", "ADMIN", "ACTIVE", LocalDateTime.now()));
        USERS.put(2L, new User(2L, "Jane Smith", "jane.smith@example.com", "USER", "ACTIVE", LocalDateTime.now()));
        USERS.put(3L, new User(3L, "Bob Johnson", "bob.johnson@example.com", "MANAGER", "ACTIVE", LocalDateTime.now()));
        USERS.put(4L, new User(4L, "Alice Brown", "alice.brown@example.com", "USER", "INACTIVE", LocalDateTime.now()));
        USER_ID_COUNTER = 5L;
    }

    public UserController() {
        System.out.println("UserController instantiated!");
    }

    /**
     * Get all users with optional filtering and pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String status) {

        System.out.println("=== UserController.getAllUsers() called ===");
        System.out.println("Parameters - page: " + page + ", size: " + size + ", search: '" + search +
                "', role: '" + role + "', status: '" + status + "'");

        List<User> filteredUsers = new ArrayList<>(USERS.values());

        // Apply filters
        if (!search.isEmpty()) {
            filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getName().toLowerCase().contains(search.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        if (!role.isEmpty()) {
            filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getRole().equalsIgnoreCase(role))
                    .toList();
        }

        if (!status.isEmpty()) {
            filteredUsers = filteredUsers.stream()
                    .filter(user -> user.getStatus().equalsIgnoreCase(status))
                    .toList();
        }

        // Apply pagination
        int totalElements = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<User> pageContent = startIndex < totalElements ?
                filteredUsers.subList(startIndex, endIndex) : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageContent);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("first", page == 0);
        response.put("last", page >= totalPages - 1);

        System.out.println("Returning " + pageContent.size() + " users out of " + totalElements + " total");
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        System.out.println("=== UserController.getUserById() called with ID: " + id + " ===");

        User user = USERS.get(id);
        if (user == null) {
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        System.out.println("Found user: " + user.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        System.out.println("=== UserController.createUser() called ===");
        System.out.println("Request: " + request);

        // Validate email uniqueness
        boolean emailExists = USERS.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(request.getEmail()));

        if (emailExists) {
            System.out.println("Email already exists: " + request.getEmail());
            return ResponseEntity.badRequest().build();
        }

        Long newId = USER_ID_COUNTER++;
        User newUser = new User(
                newId,
                request.getName(),
                request.getEmail(),
                request.getRole() != null ? request.getRole() : "USER",
                "ACTIVE",
                LocalDateTime.now()
        );

        USERS.put(newId, newUser);

        System.out.println("Created user with ID: " + newId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        System.out.println("=== UserController.updateUser() called with ID: " + id + " ===");
        System.out.println("Request: " + request);

        User existingUser = USERS.get(id);
        if (existingUser == null) {
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        // Check email uniqueness (excluding current user)
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            boolean emailExists = USERS.values().stream()
                    .anyMatch(user -> !user.getId().equals(id) &&
                            user.getEmail().equalsIgnoreCase(request.getEmail()));

            if (emailExists) {
                System.out.println("Email already exists: " + request.getEmail());
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
        existingUser.setLastModified(LocalDateTime.now());

        System.out.println("Updated user: " + existingUser.getName());
        return ResponseEntity.ok(existingUser);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        System.out.println("=== UserController.deleteUser() called with ID: " + id + " ===");

        User user = USERS.remove(id);
        if (user == null) {
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        System.out.println("Deleted user: " + user.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        System.out.println("=== UserController.getUserStats() called ===");

        long totalUsers = USERS.size();
        long activeUsers = USERS.values().stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
        long inactiveUsers = totalUsers - activeUsers;

        Map<String, Long> roleStats = new HashMap<>();
        USERS.values().forEach(user ->
                roleStats.merge(user.getRole(), 1L, Long::sum)
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("roleDistribution", roleStats);
        stats.put("lastUpdated", LocalDateTime.now().toString());

        return ResponseEntity.ok(stats);
    }

    // DTO Classes
    public static class User {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime lastModified;

        public User() {}

        public User(Long id, String name, String email, String role, String status, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
            this.createdAt = createdAt;
            this.lastModified = createdAt;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getLastModified() { return lastModified; }
        public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "', role='" + role + "', status='" + status + "'}";
        }
    }

    public static class CreateUserRequest {
        private String name;
        private String email;
        private String role;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        @Override
        public String toString() {
            return "CreateUserRequest{name='" + name + "', email='" + email + "', role='" + role + "'}";
        }
    }

    public static class UpdateUserRequest {
        private String name;
        private String email;
        private String role;
        private String status;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return "UpdateUserRequest{name='" + name + "', email='" + email + "', role='" + role + "', status='" + status + "'}";
        }
    }
}