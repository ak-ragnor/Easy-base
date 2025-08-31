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

import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.api.data.engine.dto.mapper.AttributeMapper;
import com.easybase.api.data.engine.dto.mapper.CollectionMapper;
import com.easybase.common.api.dto.response.ApiPageResponse;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.service.CollectionService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;

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
@RequestMapping("/easy-base/api/collections")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CollectionController {

	@PostMapping
	public ResponseEntity<ApiResponse<CollectionDto>> createCollection(
		@RequestBody @Valid CollectionDto request) {

		Tenant tenant = _tenantService.getDefaultTenant();

		Collection collection = _collectionService.createCollection(
			tenant.getId(), request.getName(),
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

		Tenant tenant = _tenantService.getDefaultTenant();

		Collection collection = _collectionService.getCollection(
			tenant.getId(), collectionName);

		return ResponseEntity.ok(
			ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	@GetMapping
	public ResponseEntity<ApiPageResponse<CollectionDto>> listCollections(
		@PageableDefault(
			direction = Sort.Direction.DESC, size = 20, sort = "createdAt"
		)
		Pageable pageable) {

		Tenant tenant = _tenantService.getDefaultTenant();

		Page<Collection> collections = _collectionService.getCollections(
			tenant.getId(), pageable);

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
	private final TenantService _tenantService;

}