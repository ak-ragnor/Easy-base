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

package com.easybase.api.data.engine.controller;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.api.data.engine.dto.mapper.DataRecordMapper;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.service.DataRecordService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/easy-base/api/data/{collectionName}")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RecordController {

	@PostMapping
	public ResponseEntity<ApiResponse<DataRecordDto>> createRecord(
		@PathVariable("collectionName") String collectionName,
		@RequestBody @Valid DataRecordDto request) {

		log.debug("Creating record in collection: {}", collectionName);

		Tenant tenant = _tenantService.getDefaultTenant();

		DataRecord dataRecord = _dataRecordService.createRecord(
			tenant.getId(), collectionName, request.getData());

		ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(
			HttpStatus.CREATED);

		return responseEntity.body(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteRecord(
		@PathVariable("collectionName") String collectionName,
		@PathVariable("id") String id) {

		log.debug("Deleting record {} from collection: {}", id, collectionName);

		Tenant tenant = _tenantService.getDefaultTenant();

		_dataRecordService.deleteRecord(
			tenant.getId(), collectionName, UUID.fromString(id));

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<DataRecordDto>>> getAllRecords(
		@PathVariable("collectionName") String collectionName) {

		log.debug("Getting all records from collection: {}", collectionName);

		Tenant tenant = _tenantService.getDefaultTenant();

		List<DataRecord> dataRecordList = _dataRecordService.getRecords(
			tenant.getId(), collectionName);

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecordList)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> getRecord(
		@PathVariable("collectionName") String collectionName,
		@PathVariable("id") String id) {

		log.debug("Getting record {} from collection: {}", id, collectionName);

		Tenant tenant = _tenantService.getDefaultTenant();

		DataRecord dataRecord = _dataRecordService.getRecord(
			tenant.getId(), collectionName, UUID.fromString(id));

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> updateRecord(
		@PathVariable("collectionName") String collectionName,
		@PathVariable("id") String id,
		@RequestBody @Valid DataRecordDto request) {

		log.debug("Updating record {} in collection: {}", id, collectionName);

		Tenant tenant = _tenantService.getDefaultTenant();

		DataRecord dataRecord = _dataRecordService.updateRecord(
			tenant.getId(), collectionName, UUID.fromString(id),
			request.getData());

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	private final DataRecordMapper _dataRecordMapper;
	private final DataRecordService _dataRecordService;
	private final TenantService _tenantService;

}