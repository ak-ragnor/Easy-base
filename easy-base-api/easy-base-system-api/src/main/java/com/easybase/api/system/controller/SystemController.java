package com.easybase.api.system.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.system.entity.SystemInfo;
import com.easybase.system.repository.SystemInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/easy-base/api/system")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SystemController {

	private static final String _APP_VERSION = "1.0.0-SNAPSHOT";

	private static final String _DB_VERSION = "1.0";

	private static final String _SERVICE_NAME = "Easy Base Platform";

	@GetMapping("/health")
	public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "UP");
		data.put("timestamp", LocalDateTime.now());
		data.put("version", _APP_VERSION);
		data.put("service", _SERVICE_NAME);

		log.info("Health check requested");

		return ResponseEntity
				.ok(ApiResponse.success(data, "System is healthy"));
	}

	@GetMapping("/info")
	public ResponseEntity<ApiResponse<SystemInfo>> getSystemInfo() {
		SystemInfo info = _systemInfoRepository.findLatestActive()
				.orElseGet(this::_createDefaultSystemInfo);

		log.info("System info retrieved: {}", info);

		return ResponseEntity.ok(ApiResponse.success(info,
				"System information retrieved successfully"));
	}

	@PostMapping("/ping")
	public ResponseEntity<ApiResponse<String>> ping() {
		log.debug("Ping received");
		return ResponseEntity
				.ok(ApiResponse.success("Server is responding", "Pong"));
	}

	private SystemInfo _createDefaultSystemInfo() {
		SystemInfo newInfo = SystemInfo.builder().appVersion(_APP_VERSION)
				.dbVersion(_DB_VERSION).status("ACTIVE").build();

		return _systemInfoRepository.save(newInfo);
	}

	private final SystemInfoRepository _systemInfoRepository;
}
