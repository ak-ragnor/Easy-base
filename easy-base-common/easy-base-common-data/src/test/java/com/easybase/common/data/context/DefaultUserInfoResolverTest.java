/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.data.context;

import com.easybase.common.api.context.UserInfo;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for DefaultUserInfoResolver.
 *
 * @author Akhash R
 */
@ExtendWith(MockitoExtension.class)
class DefaultUserInfoResolverTest {

	private User _createMockUser(
		UUID id, String email, String displayName, boolean deleted) {

		User user = new User();

		user.setId(id);
		user.setEmail(email);
		user.setDisplayName(displayName);
		user.setDeleted(deleted);

		return user;
	}

	private DefaultUserInfoResolver _resolver;

	@BeforeEach
	void _setUp() {
		_resolver = new DefaultUserInfoResolver(_userRepository);
	}

	@Test
	void _resolve_withAnonymousUserId_returnsAnonymousUserInfo() {
		UserInfo result = _resolver.resolve("anonymous");

		Assertions.assertThat(
			result.id()
		).isEqualTo(
			"anonymous"
		);
		Assertions.assertThat(
			result.email()
		).isNull();
		Assertions.assertThat(
			result.active()
		).isFalse();
	}

	@Test
	void _resolve_withNullUserId_throwsIllegalArgumentException() {
		Assertions.assertThatThrownBy(
			() -> _resolver.resolve(null)
		).isInstanceOf(
			IllegalArgumentException.class
		);

		Assertions.assertThatThrownBy(
			() -> _resolver.resolve(null)
		).hasMessage(
			"User ID cannot be null"
		);
	}

	@Test
	void _resolve_withValidUserId_returnsResolvedUserInfo() {
		UUID userId = UUID.randomUUID();

		User mockUser = _createMockUser(
			userId, "test@example.com", "Test User", false);

		Mockito.when(
			_userRepository.findById(userId)
		).thenReturn(
			Optional.of(mockUser)
		);

		UserInfo result = _resolver.resolve(userId.toString());

		Assertions.assertThat(
			result.id()
		).isEqualTo(
			userId.toString()
		);
		Assertions.assertThat(
			result.email()
		).isEqualTo(
			"test@example.com"
		);
		Assertions.assertThat(
			result.active()
		).isTrue();
		Assertions.assertThat(
			result.displayName()
		).isEqualTo(
			"Test User"
		);
	}

	@Test
	void _resolve_with_non_existentUserId_throwsIllegalArgumentException() {
		UUID userId = UUID.randomUUID();

		Mockito.when(
			_userRepository.findById(userId)
		).thenReturn(
			Optional.empty()
		);

		Assertions.assertThatThrownBy(
			() -> _resolver.resolve(userId.toString())
		).isInstanceOf(
			IllegalArgumentException.class
		);

		Assertions.assertThatThrownBy(
			() -> _resolver.resolve(userId.toString())
		).hasMessage(
			"User not found: " + userId
		);
	}

	@Test
	void _resolve_withInvalidUuidFormat_throwsIllegalArgumentException() {
		Assertions.assertThatThrownBy(
			() -> _resolver.resolve("invalid-uuid")
		).isInstanceOf(
			IllegalArgumentException.class
		);

		Assertions.assertThatThrownBy(
			() -> _resolver.resolve("invalid-uuid")
		).hasMessageContaining(
			"Invalid user ID format"
		);
	}

	@Test
	void _resolve_withDeletedUser_returnsInactiveUserInfo() {
		UUID userId = UUID.randomUUID();

		User mockUser = _createMockUser(
			userId, "deleted@example.com", "Deleted User", true);

		Mockito.when(
			_userRepository.findById(userId)
		).thenReturn(
			Optional.of(mockUser)
		);

		UserInfo result = _resolver.resolve(userId.toString());

		Assertions.assertThat(
			result.active()
		).isFalse();
	}

	@Mock
	private UserRepository _userRepository;

}