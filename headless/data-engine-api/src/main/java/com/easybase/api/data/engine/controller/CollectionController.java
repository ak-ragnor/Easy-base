/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.controller;

import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.api.data.engine.dto.mapper.AttributeMapper;
import com.easybase.api.data.engine.dto.mapper.CollectionMapper;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.service.CollectionService;
import com.easybase.infrastructure.api.dto.response.ApiPageResponse;
import com.easybase.infrastructure.api.dto.response.ApiResponse;

import jakarta.validation.Valid;

import java.util.UUID;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for collection management operations.
 *
 * @author Akhash R
 */
@RequestMapping("/collections")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CollectionController {

	@PostMapping
	public ResponseEntity<ApiResponse<CollectionDto>> createCollection(
		@RequestBody @Valid CollectionDto request) {

		Collection collection = _collectionService.createCollection(
			_serviceContext.tenantId(), request.getName(),
			_attributeMapper.toEntity(request.getAttributes()));

		ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(
			HttpStatus.CREATED);

		return responseEntity.body(
			ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	@DeleteMapping("/{collectionId}")
	public ResponseEntity<ApiResponse<Void>> deleteCollection(
		@PathVariable UUID collectionId) {

		_collectionService.deleteCollection(collectionId);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("/{collectionName}")
	public ResponseEntity<ApiResponse<CollectionDto>> getCollection(
		@PathVariable String collectionName) {

		Collection collection = _collectionService.getCollection(
			_serviceContext.tenantId(), collectionName);

		return ResponseEntity.ok(
			ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	@GetMapping
	public ResponseEntity<ApiPageResponse<CollectionDto>> listCollections(
		@PageableDefault(
			direction = Sort.Direction.DESC, size = 20, sort = "createdAt"
		)
		Pageable pageable) {

		Page<Collection> collections = _collectionService.getCollections(
			_serviceContext.tenantId(), pageable);

		Stream<Collection> collectionsStream = collections.stream();

		return ResponseEntity.ok(
			ApiPageResponse.success(
				collectionsStream.map(
					_collectionMapper::toDto
				).toList(),
				collections));
	}

	@PutMapping("/{collectionId}")
	public ResponseEntity<ApiResponse<CollectionDto>> updateCollection(
		@PathVariable("collectionId") UUID collectionId,
		@RequestBody @Valid CollectionDto request) {

		log.debug("Updating collection: {}", collectionId);

		Collection collection = _collectionService.updateCollection(
			collectionId, _attributeMapper.toEntity(request.getAttributes()));

		return ResponseEntity.ok(
			ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	private final AttributeMapper _attributeMapper;
	private final CollectionMapper _collectionMapper;
	private final CollectionService _collectionService;
	private final ServiceContext _serviceContext;

}