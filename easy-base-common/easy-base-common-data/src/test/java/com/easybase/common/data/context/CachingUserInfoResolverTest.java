/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.data.context;

import com.easybase.common.api.context.UserInfo;
import com.easybase.common.api.context.UserInfoResolver;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for CachingUserInfoResolver.
 *
 * @author Akhash R
 */
@ExtendWith(MockitoExtension.class)
class CachingUserInfoResolverTest {

	@Mock
	private UserInfoResolver _delegate;

	private CachingUserInfoResolver _resolver;

	@Mock
	private UserInfo _userInfo;

	@BeforeEach
	void _setUp() {
		_resolver = new CachingUserInfoResolver(_delegate);
	}

	@Test
	void _resolve_firstCall_delegatesToUnderlyingResolver() {
		String userId = "test-user-id";

		Mockito.when(
			_delegate.resolve(userId)
		).thenReturn(
			_userInfo
		);

		UserInfo result = _resolver.resolve(userId);

		Assertions.assertThat(
			result
		).isEqualTo(
			_userInfo
		);

		Mockito.verify(
			_delegate, Mockito.times(1)
		).resolve(
			userId
		);
	}

	@Test
	void _resolve_secondCall_returnsCachedResult() {
		String userId = "test-user-id";

		Mockito.when(
			_delegate.resolve(userId)
		).thenReturn(
			_userInfo
		);

		UserInfo firstResult = _resolver.resolve(userId);
		UserInfo secondResult = _resolver.resolve(userId);

		Assertions.assertThat(
			firstResult
		).isEqualTo(
			_userInfo
		);
		Assertions.assertThat(
			secondResult
		).isEqualTo(
			_userInfo
		);

		Assertions.assertThat(
			firstResult
		).isSameAs(
			secondResult
		);

		Mockito.verify(
			_delegate, Mockito.times(1)
		).resolve(
			userId
		);
	}

	@Test
	void _resolve_differentUserIds_cachesIndependently() {
		String userId1 = "user-1";
		String userId2 = "user-2";
		UserInfo userInfo2 = UserInfo.anonymous();

		Mockito.when(
			_delegate.resolve(userId1)
		).thenReturn(
			_userInfo
		);

		Mockito.when(
			_delegate.resolve(userId2)
		).thenReturn(
			userInfo2
		);

		UserInfo result1 = _resolver.resolve(userId1);
		UserInfo result2 = _resolver.resolve(userId2);

		UserInfo cachedResult1 = _resolver.resolve(userId1);

		Assertions.assertThat(
			result1
		).isEqualTo(
			_userInfo
		);
		Assertions.assertThat(
			result2
		).isEqualTo(
			userInfo2
		);
		Assertions.assertThat(
			cachedResult1
		).isSameAs(
			result1
		);

		Mockito.verify(
			_delegate, Mockito.times(1)
		).resolve(
			userId1
		);

		Mockito.verify(
			_delegate, Mockito.times(1)
		).resolve(
			userId2
		);
	}

}