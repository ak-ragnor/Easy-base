package com.easybase.core.user.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;
import com.easybase.core.user.repository.UserCredentialRepository;
import com.easybase.core.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	@Transactional
	public UserCredential addUserCredential(UUID userId, String type,
			Map<String, Object> credentialData) {

		User user = _getUser(userId);

		_userCredentialRepository.findByUserIdAndType(userId, type)
				.ifPresent(existing -> {
					throw new ConflictException("UserCredential", "type", type);
				});

		UserCredential credential = UserCredential.builder().user(user)
				.passwordType(type).credentialData(credentialData).build();

		credential = _userCredentialRepository.save(credential);

		log.info("Added credential type={} userId={}", type, userId);

		return credential;
	}

	@Transactional
	public User createUser(String email, String firstName, String lastName,
			String displayName, UUID tenantId) {

		_validateUserEmail(email, tenantId);

		Tenant tenant = _getTenant(tenantId);

		User user = User.builder().email(email).firstName(firstName)
				.lastName(lastName).displayName(displayName).tenant(tenant)
				.build();

		user = _userRepository.save(user);

		log.info("Created user with email={} id={} tenant={}", email,
				user.getId(), tenantId);

		return user;
	}

	@Transactional
	public void deleteUser(UUID id) {
		User user = _getUser(id);

		user.setIsDeleted(true);
		_userRepository.save(user);

		log.info("Soft deleted user id={}", id);
	}

	@Transactional(readOnly = true)
	public User getUser(UUID id) {
		return _getUser(id);
	}

	@Transactional(readOnly = true)
	public User getUser(String email, UUID tenantId) {
		return _userRepository.findActiveByEmailAndTenantId(email, tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("User",
						"email", email));
	}

	@Transactional(readOnly = true)
	public List<User> getUsers(UUID tenantId) {
		return _userRepository.findActiveByTenantId(tenantId);
	}

	@Transactional(readOnly = true)
	public UserCredential getUserCredential(UUID userId,
			String credentialType) {
		return _userCredentialRepository
				.findByUserIdAndType(userId, credentialType).orElseThrow(
						() -> new ResourceNotFoundException("UserCredential",
								"type", credentialType));
	}

	@Transactional(readOnly = true)
	public List<UserCredential> getUserCredentials(UUID userId) {
		return _userCredentialRepository.findByUserId(userId);
	}

	@Transactional
	public void removeUserCredential(UUID userId, String credentialType) {
		UserCredential credential = _userCredentialRepository
				.findByUserIdAndType(userId, credentialType).orElseThrow(
						() -> new ResourceNotFoundException("UserCredential",
								"type", credentialType));

		_userCredentialRepository.delete(credential);

		log.info("Removed credential type={} userId={}", credentialType,
				userId);
	}

	@Transactional
	public User updateUser(UUID id, String firstName, String lastName,
			String displayName) {

		User user = _getUser(id);

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);

		user = _userRepository.save(user);
		log.info("Updated user id={}", id);

		return user;
	}

	private User _getUser(UUID id) {
		return _userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("User", "id", id));
	}

	private Tenant _getTenant(UUID tenantId) {
		return _tenantRepository.findById(tenantId).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "id", tenantId));
	}

	private void _validateUserEmail(String email, UUID tenantId) {
		boolean exists = _userRepository.existsByEmailAndTenantId(email,
				tenantId);

		if (exists) {
			throw new ConflictException("User", "email", email);
		}
	}

	private final UserRepository _userRepository;

	private final UserCredentialRepository _userCredentialRepository;

	private final TenantRepository _tenantRepository;
}
