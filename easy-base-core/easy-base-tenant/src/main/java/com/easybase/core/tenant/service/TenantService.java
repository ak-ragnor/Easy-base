package com.easybase.core.tenant.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

	@Transactional
	public Tenant getDefaultTenant() {
		Optional<Tenant> tenantOptional = fetchTenant("default");

		if (tenantOptional.isEmpty()) {
			try {
				return createTenant("default");
			} catch (ConflictException e) {
				tenantOptional = fetchTenant("default");

				if (tenantOptional.isEmpty()) {
					throw e;
				}
			}
		}

		return tenantOptional.get();
	}

	@Transactional
	public Tenant createTenant(String name) {
		if (_tenantRepository.existsByName(name)) {
			throw new ConflictException("Tenant", "name", name);
		}

		Tenant tenant = Tenant.builder().name(name).build();

		tenant = _tenantRepository.save(tenant);

		log.info("Created tenant '{}' with ID {}", name, tenant.getId());

		return tenant;
	}

	public Optional<Tenant> fetchTenant(String name) {
		return _tenantRepository.findByName(name);
	}

	public Tenant getTenant(String name) {
		return _tenantRepository.findByName(name).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "name", name));
	}

	public Tenant getTenant(UUID id) {
		return _tenantRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "id", id));
	}

	public List<Tenant> getTenants() {
		return _tenantRepository.findAll();
	}

	@Transactional
	public Tenant updateTenant(UUID id, String name) {
		Tenant tenant = getTenant(id);

		if (_tenantRepository.existsByName(name)
				&& !tenant.getName().equals(name)) {
			throw new ConflictException("Tenant", "name", name);
		}

		tenant.setName(name);
		tenant = _tenantRepository.save(tenant);

		log.info("Updated tenant with ID {} to name '{}'", id, name);

		return tenant;
	}

	@Transactional
	public void deleteTenant(UUID tenantId) {
		Tenant tenant = _tenantRepository.findById(tenantId).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "id", tenantId));

		tenant.setIsDeleted(true);
		_tenantRepository.save(tenant);

		log.info("Soft deleted tenant '{}' ({})", tenant.getName(), tenantId);
	}

	private final TenantRepository _tenantRepository;
}
