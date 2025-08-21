package com.easybase.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.easybase.system.entity.SystemInfo;

@Repository
public interface SystemInfoRepository extends JpaRepository<SystemInfo, Long> {

	@Query("SELECT s FROM SystemInfo s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
	Optional<SystemInfo> findLatestActive();

	Optional<SystemInfo> findTopByOrderByCreatedAtDesc();
}
