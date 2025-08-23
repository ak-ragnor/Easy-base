package com.easybase.core.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.easybase.core.user.entity.UserCredential;

@Repository
public interface UserCredentialRepository
		extends JpaRepository<UserCredential, Long> {

	List<UserCredential> findByUserId(UUID userId);

	@Query("SELECT uc FROM UserCredential uc WHERE uc.user.id = :userId AND uc.passwordType = :type")
	Optional<UserCredential> findByUserIdAndType(@Param("userId") UUID userId,
			@Param("type") String type);

	@Query("SELECT uc FROM UserCredential uc WHERE uc.user.email = :email AND uc.passwordType = :type")
	Optional<UserCredential> findByUserEmailAndType(
			@Param("email") String email, @Param("type") String type);

	void deleteByUserId(UUID userId);
}
