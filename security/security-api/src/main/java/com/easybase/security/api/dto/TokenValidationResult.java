/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.dto;

import java.util.Optional;

import lombok.Data;

/**
 * @author Akhash
 */
@Data
public class TokenValidationResult {

	public static TokenValidationResult invalid(String reason) {
		TokenValidationResult tokenValidationResult =
			new TokenValidationResult();

		tokenValidationResult.setValid(false);
		tokenValidationResult.setReason(reason);

		return tokenValidationResult;
	}

	public static TokenValidationResult valid(TokenClaims claims) {
		TokenValidationResult tokenValidationResult =
			new TokenValidationResult();

		tokenValidationResult.setValid(true);
		tokenValidationResult.setClaims(claims);

		return tokenValidationResult;
	}

	public Optional<TokenClaims> getClaims() {
		return Optional.ofNullable(claims);
	}

	public Optional<String> getReason() {
		return Optional.ofNullable(reason);
	}

	private TokenClaims claims;
	private String reason;
	private boolean valid;

}