package com.easybase.core.dataengine.service;

import com.easybase.core.dataengine.entity.Tenant;
import com.easybase.core.dataengine.repository.TenantRepository;
import com.easybase.core.dataengine.service.ddl.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final SchemaManager schemaManager;

    @Transactional
    public Tenant createTenant(String name) {
        if (tenantRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tenant with name '" + name + "' already exists");
        }

        Tenant tenant = Tenant.builder().name(name).build();

        tenant = tenantRepository.save(tenant);

        schemaManager.createSchemaIfNotExists(tenant.getId());
        log.info("Created tenant '{}' with ID {}", name, tenant.getId());

        return tenant;
    }

    @Transactional
    public void dropTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        schemaManager.dropSchemaIfExists(tenantId);
        tenantRepository.delete(tenant);

        log.info("Dropped tenant '{}' ({})", tenant.getName(), tenantId);
    }
}
