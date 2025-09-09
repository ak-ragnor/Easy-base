/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.authentication;

import com.easybase.security.api.dto.AuthenticatedPrincipalData;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Akhash
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	public JwtAuthenticationToken(
		AuthenticatedPrincipalData principal,
		Collection<? extends GrantedAuthority> authorities) {

		super(authorities);

		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public AuthenticatedPrincipalData getPrincipal() {
		return principal;
	}

	private final AuthenticatedPrincipalData principal;

}