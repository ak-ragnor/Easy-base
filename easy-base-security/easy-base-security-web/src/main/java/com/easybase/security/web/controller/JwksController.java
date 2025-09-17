/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.controller;

import com.easybase.security.jwt.service.KeyManager;

import java.math.BigInteger;

import java.security.interfaces.RSAPublicKey;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Akhash
 */
@RequestMapping("/.well-known")
@RequiredArgsConstructor
@RestController
public class JwksController {

	@GetMapping("/jwks.json")
	public Map<String, Object> getJwks() {
		RSAPublicKey publicKey = (RSAPublicKey)keyManager.getPublicKey(
			keyManager.getCurrentKeyId());

		BigInteger modulus = publicKey.getModulus();

		byte[] modulusBytes = modulus.toByteArray();

		Base64.Encoder urlEncoder = Base64.getUrlEncoder();

		urlEncoder = urlEncoder.withoutPadding();

		String n = urlEncoder.encodeToString(modulusBytes);

		BigInteger publicExponent = publicKey.getPublicExponent();

		byte[] exponentBytes = publicExponent.toByteArray();

		String e = urlEncoder.encodeToString(exponentBytes);

		Map<String, Object> jwk = Map.of(
			"kty", "RSA", "use", "sig", "kid", keyManager.getCurrentKeyId(),
			"alg", "RS256", "n", n, "e", e);

		return Map.of("keys", List.of(jwk));
	}

	private final KeyManager keyManager;

}