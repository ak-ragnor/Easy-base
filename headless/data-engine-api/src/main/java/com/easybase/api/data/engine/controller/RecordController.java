/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.controller;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.api.data.engine.dto.mapper.DataRecordMapper;
import com.easybase.common.util.PageUtil;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.domain.entity.DataRecord;
import com.easybase.core.data.engine.service.CollectionLocalService;
import com.easybase.core.data.engine.service.DataRecordService;
import com.easybase.core.search.SearchService;
import com.easybase.infrastructure.api.dto.response.ApiPageResponse;
import com.easybase.infrastructure.api.dto.response.ApiResponse;
import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping
	public ResponseEntity<ApiPageResponse<DataRecordDto>> getRecords(
		@PathVariable("collectionName") String collectionName,
		@RequestParam(required = false) String filter,
		@RequestParam(required = false) String search,
		@RequestParam(required = false) List<String> fields,
		@PageableDefault(
			direction = Sort.Direction.DESC, size = 20, sort = "createdAt"
		)
		Pageable pageable) {

		log.debug(
			"Querying records from collection: {} filter={} search={} " +
				"sort={} page={} size={}",
			collectionName, filter, search, pageable.getSort(),
			pageable.getPageNumber(), pageable.getPageSize());

		Collection collection = _collectionLocalService.fetchCollection(
			_serviceContext.tenantId(), collectionName);

		SearchContext context = SearchContext.builder(
		).entityType(
			"records"
		).tenantId(
			_serviceContext.tenantId()
		).filter(
			filter
		).search(
			search
		).sort(
			PageUtil.sortFrom(pageable)
		).page(
			pageable.getPageNumber()
		).size(
			pageable.getPageSize()
		).checkPermission(
			false
		).build();

		context.setAttribute("collection", collection);

		if ((fields != null) && !fields.isEmpty()) {
			context.setAttribute("fields", fields);
		}

		QueryResult<DataRecordDto> result =
			_searchService.<DataRecord, DataRecordDto>search(
				context, record -> _dataRecordMapper.toDto(record));

		return ResponseEntity.ok(
			ApiPageResponse.success(
				result.getContent(), PageUtil.from(result)));
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

	private final CollectionLocalService _collectionLocalService;
	private final DataRecordMapper _dataRecordMapper;
	private final DataRecordService _dataRecordService;
	private final SearchService _searchService;
	private final ServiceContext _serviceContext;

}