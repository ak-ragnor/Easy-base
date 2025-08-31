/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.api.system.controller;

import com.easybase.common.api.dto.response.ApiResponse;
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
		SystemInfo newInfo = SystemInfo.builder(
		).appVersion(
			_APP_VERSION
		).dbVersion(
			_DB_VERSION
		).status(
			"ACTIVE"
		).build();

		return _systemInfoRepository.save(newInfo);
	}

	private static final String _APP_VERSION = "1.0.0-SNAPSHOT";

	private static final String _DB_VERSION = "1.0";

	private static final String _SERVICE_NAME = "Easy Base Platform";

	private final SystemInfoRepository _systemInfoRepository;

}