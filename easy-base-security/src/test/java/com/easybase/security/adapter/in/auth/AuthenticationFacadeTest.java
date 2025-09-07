/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.in.auth;

import com.easybase.common.exception.InvalidTokenException;
import com.easybase.security.domain.model.AuthResult;
import com.easybase.security.domain.port.in.TokenValidator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for AuthenticationFacade.
 *
 * @author Akhash R
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationFacadeTest {

	@Test
	public void authenticateWithApiKeyHeaderExtractsCorrectToken()
		throws Exception {

		Mockito.when(
			_request.getHeader("Authorization")
		).thenReturn(
			null
		);
		Mockito.when(
			_request.getHeader("X-Api-Key")
		).thenReturn(
			"api-key-123"
		);
		Mockito.when(
			_validator1.supports(_request)
		).thenReturn(
			true
		);
		Mockito.when(
			_validator1.validate(_request, "api-key-123")
		).thenReturn(
			_authResult
		);

		AuthResult result = _facade.authenticate(_request);

		Assertions.assertThat(
			result
		).isEqualTo(
			_authResult
		);
	}

	@Test
	public void authenticateWithNoSupportingValidatorThrowsInvalidTokenException() {
		Mockito.when(
			_validator1.supports(_request)
		).thenReturn(
			false
		);
		Mockito.when(
			_validator2.supports(_request)
		).thenReturn(
			false
		);

		Assertions.assertThatThrownBy(
			() -> _facade.authenticate(_request)
		).isInstanceOf(
			InvalidTokenException.class
		);

		Assertions.assertThatThrownBy(
			() -> _facade.authenticate(_request)
		).hasMessage(
			"No authentication strategy matched request"
		);
	}

	@Test
	public void authenticateWithNoTokenThrowsInvalidTokenException() {
		Mockito.when(
			_request.getHeader("Authorization")
		).thenReturn(
			null
		);
		Mockito.when(
			_request.getHeader("X-Api-Key")
		).thenReturn(
			null
		);
		Mockito.when(
			_validator1.supports(_request)
		).thenReturn(
			true
		);

		Assertions.assertThatThrownBy(
			() -> _facade.authenticate(_request)
		).isInstanceOf(
			InvalidTokenException.class
		);

		Assertions.assertThatThrownBy(
			() -> _facade.authenticate(_request)
		).hasMessage(
			"No token found in supported request"
		);
	}

	@Test
	public void authenticateWithSecondValidatorSupportingReturnsAuthResult()
		throws Exception {

		Mockito.when(
			_request.getHeader("Authorization")
		).thenReturn(
			"Basic token456"
		);
		Mockito.when(
			_validator1.supports(_request)
		).thenReturn(
			false
		);
		Mockito.when(
			_validator2.supports(_request)
		).thenReturn(
			true
		);
		Mockito.when(
			_validator2.validate(_request, "token456")
		).thenReturn(
			_authResult
		);

		AuthResult result = _facade.authenticate(_request);

		Assertions.assertThat(
			result
		).isEqualTo(
			_authResult
		);
	}

	@Test
	public void authenticateWithSupportingValidatorReturnsAuthResult()
		throws Exception {

		Mockito.when(
			_request.getHeader("Authorization")
		).thenReturn(
			"Bearer token123"
		);
		Mockito.when(
			_validator1.supports(_request)
		).thenReturn(
			true
		);
		Mockito.when(
			_validator1.validate(_request, "token123")
		).thenReturn(
			_authResult
		);

		AuthResult result = _facade.authenticate(_request);

		Assertions.assertThat(
			result
		).isEqualTo(
			_authResult
		);
	}

	@BeforeEach
	void _setUp() {
		_facade = new AuthenticationFacade(List.of(_validator1, _validator2));
	}

	@Mock
	private AuthResult _authResult;

	private AuthenticationFacade _facade;

	@Mock
	private HttpServletRequest _request;

	@Mock
	private TokenValidator _validator1;

	@Mock
	private TokenValidator _validator2;

}