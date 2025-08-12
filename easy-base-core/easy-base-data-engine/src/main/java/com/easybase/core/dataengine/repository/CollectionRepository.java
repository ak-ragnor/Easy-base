package com.easybase.core.dataengine.repository;

import com.easybase.core.dataengine.entity.Collection;
import com.easybase.core.dataengine.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {

    Optional<Collection> findByTenantAndName(Tenant tenant, String name);

    Optional<Collection> findByTenantIdAndName(UUID tenantId, String name);

    List<Collection> findByTenant(Tenant tenant);

    List<Collection> findByTenantId(UUID tenantId);

    boolean existsByTenantAndName(Tenant tenant, String name);

    boolean existsByTenantIdAndName(UUID tenantId, String name);
}