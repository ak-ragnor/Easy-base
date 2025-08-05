package com.easybase.api.controller;

import com.easybase.core.entity.SystemInfo;
import com.easybase.core.repository.SystemInfoRepository;
import com.easybase.api.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/easy-base/api/system")
@Slf4j
@CrossOrigin(origins = "*")
public class SystemController {

    private final SystemInfoRepository systemInfoRepository;

    public SystemController(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0-SNAPSHOT");
        data.put("service", "EasyBase Platform");

        log.info("Health check requested at {}", LocalDateTime.now());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("System is healthy")
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<SystemInfo>> getSystemInfo() {
        try {
            SystemInfo info = systemInfoRepository.findLatestActive()
                    .orElseGet(() -> {
                        SystemInfo newInfo = SystemInfo.builder()
                                .appVersion("1.0.0-SNAPSHOT")
                                .dbVersion("1.0")
                                .status("ACTIVE")
                                .build();
                        return systemInfoRepository.save(newInfo);
                    });

            log.info("System info retrieved: {}", info);

            return ResponseEntity.ok(
                    ApiResponse.<SystemInfo>builder()
                            .success(true)
                            .message("System information retrieved successfully")
                            .data(info)
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving system info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<SystemInfo>builder()
                            .success(false)
                            .message("Error retrieving system information")
                            .timestamp(LocalDateTime.now())
                            .build()
                    );
        }
    }

    @PostMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        log.debug("Ping received");
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Pong")
                        .data("Server is responding")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}