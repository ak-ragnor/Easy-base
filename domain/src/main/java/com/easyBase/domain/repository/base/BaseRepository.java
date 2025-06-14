package com.easyBase.domain.repository.base;

import com.easyBase.domain.entity.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Base Repository Interface
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Find entities created after a specific date
     * Custom query for performance optimization
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt > :createdAfter ORDER BY e.createdAt DESC")
    Page<T> findByCreatedAfter(@Param("createdAfter") ZonedDateTime createdAfter, Pageable pageable);

    /**
     * Count entities created after a specific date
     * Essential for dashboard statistics
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.createdAt > :createdAfter")
    long countByCreatedAfter(@Param("createdAfter") ZonedDateTime createdAfter);

    /**
     * Bulk update timestamps for audit purposes
     * Performance-critical bulk operation
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.lastModified = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    int touchEntities(@Param("ids") List<ID> ids);

    /**
     * Find the most recently created entity
     * Often needed for dashboard "latest" displays
     */
    @Query("SELECT e FROM #{#entityName} e ORDER BY e.createdAt DESC LIMIT 1")
    Optional<T> findMostRecentlyCreated();

    // ===== DYNAMIC QUERY SUPPORT =====
    // Inherited from JpaSpecificationExecutor<T>:
    // - Page<T> findAll(Specification<T> spec, Pageable pageable)
    // - List<T> findAll(Specification<T> spec)
    // - long count(Specification<T> spec)
    // - Optional<T> findOne(Specification<T> spec)

    // ===== HYBRID HELPER METHODS =====

    /**
     * Save and refresh entity immediately
     * Combines custom behavior with JPA operations
     */
    default T saveAndRefresh(T entity) {
        return saveAndFlush(entity);
    }
}