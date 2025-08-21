package com.easybase.api.system.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.system.entity.SystemInfo;
import com.easybase.system.repository.SystemInfoRepository;

import lombok.extern.slf4j.Slf4j;

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

		return ResponseEntity
				.ok(ApiResponse.success(data, "System is healthy"));
	}

	@GetMapping("/info")
	public ResponseEntity<ApiResponse<SystemInfo>> getSystemInfo() {
		try {
			SystemInfo info = systemInfoRepository.findLatestActive()
					.orElseGet(() -> {
						SystemInfo newInfo = SystemInfo.builder()
								.appVersion("1.0.0-SNAPSHOT").dbVersion("1.0")
								.status("ACTIVE").build();
						return systemInfoRepository.save(newInfo);
					});

			log.info("System info retrieved: {}", info);

			return ResponseEntity.ok(ApiResponse.success(info,
					"System information retrieved successfully"));
		} catch (Exception e) {
			log.error("Error retrieving system info", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.failure("Error retrieving system information",
							null, 500));
		}
	}

	@PostMapping("/ping")
	public ResponseEntity<ApiResponse<String>> ping() {
		log.debug("Ping received");

		return ResponseEntity
				.ok(ApiResponse.success("Server is responding", "Pong"));
	}
}
