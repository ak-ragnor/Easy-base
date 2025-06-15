package com.easyBase.domain.repository.jpa.site;

import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.site.UserSiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSiteRepository extends JpaRepository<UserSite, UserSiteId> {

    Optional<UserSite> findByUserIdAndSiteId(Long userId, Long siteId);

    @Query("SELECT us FROM UserSite us WHERE us.site.id = :siteId AND us.isActive = true")
    List<UserSite> findBySiteId(@Param("siteId") Long siteId);

    @Query("SELECT us FROM UserSite us WHERE us.user.id = :userId AND us.isActive = true")
    List<UserSite> findByUserId(@Param("userId") Long userId);

    Optional<UserSite> findByUserIdAndSiteIdAndRole(Long userId, Long siteId, UserRole role);

    boolean existsByUserIdAndSiteIdAndIsActive(Long userId, Long siteId, Boolean isActive);

    List<UserSite> findBySiteIdAndRoleAndIsActive(Long siteId, UserRole role, Boolean isActive);

    long countBySiteIdAndIsActive(Long siteId, Boolean isActive);

    @Modifying
    @Query("UPDATE UserSite us SET us.isActive = false, us.lastModified = CURRENT_TIMESTAMP WHERE us.user.id = :userId AND us.site.id = :siteId")
    void deactivateUserSite(@Param("userId") Long userId, @Param("siteId") Long siteId);

    void deleteByUser_IdAndSite_Id(Long userId, Long siteId);

    @Query("SELECT us FROM UserSite us LEFT JOIN FETCH us.user LEFT JOIN FETCH us.site WHERE us.user.id = :userId AND us.site.id = :siteId")
    Optional<UserSite> findByUserIdAndSiteIdWithDetails(@Param("userId") Long userId, @Param("siteId") Long siteId);

    @Query("SELECT us FROM UserSite us LEFT JOIN FETCH us.user WHERE us.site.id = :siteId AND us.isActive = true")
    List<UserSite> findBySiteIdWithUserDetails(@Param("siteId") Long siteId);

}