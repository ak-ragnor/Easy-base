package com.easybase.core.data.engine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.easybase.core.data.engine.entity.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

	Optional<Tenant> findByName(String name);

	boolean existsByName(String name);
}
