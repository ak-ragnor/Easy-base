package com.easybase.security.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;

public interface LoadUserPort {

	Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	Optional<User> findById(UUID userId);

	Optional<UserCredential> findCredentialByUserId(UUID userId,
			String credentialType);

	boolean verifyPassword(String plainPassword, String hashedPassword);
}
