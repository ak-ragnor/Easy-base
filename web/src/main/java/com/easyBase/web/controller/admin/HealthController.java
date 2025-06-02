package com.easyBase.web.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status
 */
@RestController
@RequestMapping("/admin")  // Added base path to work with /api/* servlet mapping
public class HealthController {

    public HealthController() {
        System.out.println("HealthController instantiated!");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        System.out.println("=== HealthController.health() called! ===");

        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("application", "Easy Base");
        status.put("version", "1.0.0-SNAPSHOT");
        status.put("profile", System.getProperty("spring.profiles.active", "dev"));
        status.put("memory", getMemoryInfo());
        status.put("message", "Application is running successfully");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        System.out.println("=== HealthController.info() called! ===");

        Map<String, Object> info = new HashMap<>();
        info.put("app", "Easy Base Application");
        info.put("description", "Spring Framework + React Frontend");
        info.put("version", "1.0.0-SNAPSHOT");
        info.put("java.version", System.getProperty("java.version"));
        info.put("spring.profiles.active", System.getProperty("spring.profiles.active", "dev"));
        info.put("build.timestamp", LocalDateTime.now().toString());
        info.put("context.path", "/easy-base");

        return ResponseEntity.ok(info);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        System.out.println("=== HealthController.status() called! ===");

        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Application is running");
        status.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(status);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("=== HealthController.ping() called! ===");
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("=== HealthController.test() called! ===");
        return ResponseEntity.ok("Controller is working!");
    }

    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memory = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        memory.put("total", formatBytes(runtime.totalMemory()));
        memory.put("free", formatBytes(runtime.freeMemory()));
        memory.put("used", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        memory.put("max", formatBytes(runtime.maxMemory()));

        return memory;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}