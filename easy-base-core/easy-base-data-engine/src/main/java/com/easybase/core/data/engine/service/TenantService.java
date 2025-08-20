package com.easybase.core.data.engine.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.Tenant;
import com.easybase.core.data.engine.repository.TenantRepository;
import com.easybase.core.data.engine.service.ddl.SchemaManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

	private final TenantRepository _tenantRepository;

	private final SchemaManager _schemaManager;

	@Transactional
	public Tenant getDeafultTenant() {
		Tenant tenant = findByName("default");

		if (tenant == null) {
			tenant = createTenant("default");
		}

		return tenant;
	}

	@Transactional
	public Tenant createTenant(String name) {
		if (_tenantRepository.existsByName(name)) {
			throw new ConflictException("Tenant", "name", name);
		}

		Tenant tenant = Tenant.builder().name(name).build();
		tenant = _tenantRepository.save(tenant);

		_schemaManager.createSchemaIfNotExists(tenant.getId());
		log.info("Created tenant '{}' with ID {}", name, tenant.getId());

		return tenant;
	}

	public Tenant findByName(String name) {
		return _tenantRepository.findByName(name).orElse(null);
	}

	public Tenant findById(UUID id) {
		return _tenantRepository.findById(id).orElse(null);
	}

	@Transactional
	public void dropTenant(UUID tenantId) {
		Tenant tenant = _tenantRepository.findById(tenantId).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "id", tenantId));

		_schemaManager.dropSchemaIfExists(tenantId);
		_tenantRepository.delete(tenant);

		log.info("Dropped tenant '{}' ({})", tenant.getName(), tenantId);
	}

}
