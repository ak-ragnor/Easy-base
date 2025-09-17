/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.controller;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.api.data.engine.dto.mapper.DataRecordMapper;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.service.DataRecordService;
import com.easybase.infrastructure.api.dto.response.ApiResponse;

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

/**
 * @author Akhash R
 */
@RequestMapping("/data/{collectionName}")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RecordController {

	@PostMapping
	public ResponseEntity<ApiResponse<DataRecordDto>> createRecord(
		@PathVariable("collectionName") String collectionName,
		@RequestBody @Valid DataRecordDto request) {

		log.debug("Creating record in collection: {}", collectionName);

		DataRecord dataRecord = _dataRecordService.createRecord(
			_serviceContext.tenantId(), collectionName, request.getData());

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

		_dataRecordService.deleteRecord(
			_serviceContext.tenantId(), collectionName, UUID.fromString(id));

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<DataRecordDto>>> getAllRecords(
		@PathVariable("collectionName") String collectionName) {

		log.debug("Getting all records from collection: {}", collectionName);

		List<DataRecord> dataRecordList = _dataRecordService.getRecords(
			_serviceContext.tenantId(), collectionName);

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecordList)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> getRecord(
		@PathVariable("collectionName") String collectionName,
		@PathVariable("id") String id) {

		log.debug("Getting record {} from collection: {}", id, collectionName);

		DataRecord dataRecord = _dataRecordService.getRecord(
			_serviceContext.tenantId(), collectionName, UUID.fromString(id));

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DataRecordDto>> updateRecord(
		@PathVariable("collectionName") String collectionName,
		@PathVariable("id") String id,
		@RequestBody @Valid DataRecordDto request) {

		log.debug("Updating record {} in collection: {}", id, collectionName);

		DataRecord dataRecord = _dataRecordService.updateRecord(
			_serviceContext.tenantId(), collectionName, UUID.fromString(id),
			request.getData());

		return ResponseEntity.ok(
			ApiResponse.success(_dataRecordMapper.toDto(dataRecord)));
	}

	private final DataRecordMapper _dataRecordMapper;
	private final DataRecordService _dataRecordService;
	private final ServiceContext _serviceContext;

}