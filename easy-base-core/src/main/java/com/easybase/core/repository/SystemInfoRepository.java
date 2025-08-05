package com.easybase.core.repository;

import com.easybase.core.entity.SystemInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SystemInfoRepository extends JpaRepository<SystemInfo, Long> {

    @Query("SELECT s FROM SystemInfo s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Optional<SystemInfo> findLatestActive();

    Optional<SystemInfo> findTopByOrderByCreatedAtDesc();
}
