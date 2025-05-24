package com.easyBase.web.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("application", "Easy Base");
        status.put("version", "1.0.0-SNAPSHOT");
        status.put("profile", System.getProperty("spring.profiles.active", "dev"));
        return ResponseEntity.ok(status);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("app", "Easy Base Application");
        info.put("description", "Spring Framework + React Frontend");
        info.put("version", "1.0.0-SNAPSHOT");
        info.put("java.version", System.getProperty("java.version"));
        info.put("spring.profiles.active", System.getProperty("spring.profiles.active", "dev"));
        return ResponseEntity.ok(info);
    }
}