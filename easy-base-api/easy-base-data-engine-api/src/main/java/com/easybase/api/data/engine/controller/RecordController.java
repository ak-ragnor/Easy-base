package com.easybase.api.data.engine.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.api.data.engine.dto.mapper.DataRecordMapper;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.service.DataRecordService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/easy-base/api/data/{collectionName}")
@RequiredArgsConstructor
@Slf4j
public class RecordController {

	private final DataRecordService _dataRecordService;
	private final TenantService _tenantService;
	private final DataRecordMapper _dataRecordMapper;

	@PostMapping
	public ResponseEntity<ApiResponse<DataRecordDto>> createRecord(
			@PathVariable("collectionName") String collectionName,
			@Valid @RequestBody DataRecordDto request) {

		log.debug("Creating record in collection: {}", collectionName);

		Tenant defaultTenant = _tenantService.getDefaultTenant();

		DataRecord dataRecord = _dataRecordService.createRecord(
				defaultTenant.getId(), collectionName, request.getData());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> getRecord(
			@PathVariable("collectionName") String collectionName,
			@PathVariable("id") String id) {

		log.debug("Getting record {} from collection: {}", id, collectionName);

		Tenant defaultTenant = _tenantService.getDefaultTenant();
		UUID recordId = UUID.fromString(id);

		DataRecord dataRecord = _dataRecordService
				.getRecord(defaultTenant.getId(), collectionName, recordId);

		return ResponseEntity
				.ok(ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<DataRecordDto>>> getAllRecords(
			@PathVariable("collectionName") String collectionName) {

		log.debug("Getting all records from collection: {}", collectionName);

		Tenant defaultTenant = _tenantService.getDefaultTenant();

		List<DataRecord> dataRecordList = _dataRecordService
				.getRecords(defaultTenant.getId(), collectionName);

		return ResponseEntity.ok(
				ApiResponse.success(_dataRecordMapper.toDto(dataRecordList)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> updateRecord(
			@PathVariable("collectionName") String collectionName,
			@PathVariable("id") String id,
			@Valid @RequestBody DataRecordDto request) {

		log.debug("Updating record {} in collection: {}", id, collectionName);

		Tenant defaultTenant = _tenantService.getDefaultTenant();

		DataRecord dataRecord = _dataRecordService.updateRecord(
				defaultTenant.getId(), collectionName, UUID.fromString(id),
				request.getData());

		return ResponseEntity
				.ok(ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteRecord(
			@PathVariable("collectionName") String collectionName,
			@PathVariable("id") String id) {

		log.debug("Deleting record {} from collection: {}", id, collectionName);

		Tenant defaultTenant = _tenantService.getDefaultTenant();

		_dataRecordService.deleteRecord(defaultTenant.getId(), collectionName,
				UUID.fromString(id));

		return ResponseEntity.ok(ApiResponse.success(null));
	}
}
