package com.easybase.api.data.engine.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.api.data.engine.dto.mapper.AttributeMapper;
import com.easybase.api.data.engine.dto.mapper.CollectionMapper;
import com.easybase.common.api.dto.response.ApiPageResponse;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.service.CollectionService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/easy-base/api/collections")
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

	@PostMapping
	public ResponseEntity<ApiResponse<CollectionDto>> createCollection(
			@Valid @RequestBody CollectionDto request) {
		Tenant tenant = _tenantService.getDefaultTenant();

		Collection collection = _collectionService.createCollection(
				tenant.getId(), request.getName(),
				_attributeMapper.toEntity(request.getAttributes()));

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(_collectionMapper.toDto(collection)));
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

		Collection collection = _collectionService.getCollection(tenant.getId(),
				collectionName);

		return ResponseEntity
				.ok(ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	@GetMapping
	public ResponseEntity<ApiPageResponse<CollectionDto>> listCollections(
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Tenant tenant = _tenantService.getDefaultTenant();

		Page<Collection> collections = _collectionService
				.getCollections(tenant.getId(), pageable);

		List<CollectionDto> collectionDtoList = collections.stream()
				.map(_collectionMapper::toDto).toList();

		return ResponseEntity
				.ok(ApiPageResponse.success(collectionDtoList, collections));
	}

	@PutMapping("/{collectionId}")
	public ResponseEntity<ApiResponse<CollectionDto>> updateCollection(
			@PathVariable("collectionId") UUID collectionId,
			@Valid @RequestBody CollectionDto request) {

		log.debug("Updating collection: {}", collectionId);

		Collection collection = _collectionService.updateCollection(
				collectionId,
				_attributeMapper.toEntity(request.getAttributes()));

		return ResponseEntity
				.ok(ApiResponse.success(_collectionMapper.toDto(collection)));
	}

	private final AttributeMapper _attributeMapper;

	private final CollectionMapper _collectionMapper;

	private final CollectionService _collectionService;

	private final TenantService _tenantService;
}
