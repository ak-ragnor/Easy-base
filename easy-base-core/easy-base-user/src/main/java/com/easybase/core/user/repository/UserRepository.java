package com.easybase.core.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.easybase.core.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
	Optional<User> findActiveByEmail(@Param("email") String email);

	boolean existsByEmail(String email);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.isDeleted = false")
	boolean existsActiveByEmail(@Param("email") String email);

	List<User> findByTenantId(UUID tenantId);

	@Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.isDeleted = false")
	List<User> findActiveByTenantId(@Param("tenantId") UUID tenantId);

	Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.isDeleted = false")
	Optional<User> findActiveByEmailAndTenantId(@Param("email") String email,
			@Param("tenantId") UUID tenantId);

	boolean existsByEmailAndTenantId(String email, UUID tenantId);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.isDeleted = false")
	boolean existsActiveByEmailAndTenantId(@Param("email") String email,
			@Param("tenantId") UUID tenantId);
}
