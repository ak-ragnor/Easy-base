package com.easybase.core.data.engine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.tenant.entity.Tenant;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {

	Optional<Collection> findByTenantAndName(Tenant tenant, String name);

	Optional<Collection> findByTenantIdAndName(UUID tenantId, String name);

	Page<Collection> findByTenantId(UUID tenantId, Pageable pageable);

	boolean existsByTenantAndName(Tenant tenant, String name);

	boolean existsByTenantIdAndName(UUID tenantId, String name);
}
