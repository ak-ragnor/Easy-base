/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.system.controller;

import com.easybase.infrastructure.api.dto.response.ApiResponse;
import com.easybase.system.entity.SystemInfo;
import com.easybase.system.repository.SystemInfoRepository;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Akhash R
 */
@CrossOrigin(origins = "*")
@RequestMapping("/easy-base/api/system")
@RequiredArgsConstructor
@RestController
@Slf4j
public class SystemController {

	@GetMapping("/info")
	public ResponseEntity<ApiResponse<SystemInfo>> getSystemInfo() {
		Optional<SystemInfo> optionalInfo =
			_systemInfoRepository.findLatestActive();

		SystemInfo info = optionalInfo.orElseGet(
			this::_createDefaultSystemInfo);

		log.info("System info retrieved: {}", info);

		return ResponseEntity.ok(
			ApiResponse.success(
				info, "System information retrieved successfully"));
	}

	@GetMapping("/health")
	public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
		Map<String, Object> data = new HashMap<>();

		data.put("service", _SERVICE_NAME);
		data.put("status", "UP");
		data.put("timestamp", LocalDateTime.now());
		data.put("version", _APP_VERSION);

		log.info("Health check requested");

		return ResponseEntity.ok(
			ApiResponse.success(data, "System is healthy"));
	}

	@PostMapping("/ping")
	public ResponseEntity<ApiResponse<String>> ping() {
		log.debug("Ping received");

		return ResponseEntity.ok(
			ApiResponse.success("Server is responding", "Pong"));
	}

	private SystemInfo _createDefaultSystemInfo() {
		SystemInfo systemInfo = new SystemInfo();

		systemInfo.setAppVersion(_APP_VERSION);
		systemInfo.setDbVersion(_DB_VERSION);
		systemInfo.setStatus("ACTIVE");

		return _systemInfoRepository.save(systemInfo);
	}

	private static final String _APP_VERSION = "1.0.0-SNAPSHOT";

	private static final String _DB_VERSION = "1.0";

	private static final String _SERVICE_NAME = "Easy Base Platform";

	private final SystemInfoRepository _systemInfoRepository;

}