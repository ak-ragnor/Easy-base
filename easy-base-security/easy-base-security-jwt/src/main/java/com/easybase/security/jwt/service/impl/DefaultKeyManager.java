/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.jwt.service.impl;

import com.easybase.security.jwt.config.JwtProperties;
import com.easybase.security.jwt.service.KeyManager;

import java.nio.charset.StandardCharsets;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * @author Akhash
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultKeyManager implements KeyManager {

	@Override
	public String getCurrentKeyId() {
		return _jwtProperties.getKeyId();
	}

	@Override
	public KeyPair getCurrentKeyPair() {
		if (keyPair == null) {
			_initializeKeys();
		}

		return keyPair;
	}

	@Override
	public PublicKey getPublicKey(String keyId) {
		if (keyPair == null) {
			_initializeKeys();
		}

		return keyPair.getPublic();
	}

	public Key getSigningKey() {
		if (signingKey == null) {
			_initializeKeys();
		}

		return signingKey;
	}

	private void _generateDefaultRsaKeys() throws Exception {
		log.warn(
			"No RSA keys configured. Generating default keys for development. Use proper keys in production!");

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

		keyGen.initialize(2048);

		keyPair = keyGen.generateKeyPair();

		signingKey = keyPair.getPrivate();
	}

	private void _initializeHmacKey() {
		String secret = _jwtProperties.getSecretKey();

		if ((secret == null) || _isEmptyAfterTrim(secret)) {
			secret = "default-dev-secret-key-change-in-production";

			log.warn(
				"Using default HMAC secret key. Change this in production!");
		}

		signingKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

	private void _initializeKeys() {
		try {
			if (Objects.equals("HS256", _jwtProperties.getAlgorithm())) {
				_initializeHmacKey();
			}
			else {
				_initializeRsaKeys();
			}
		}
		catch (Exception exception) {
			log.error("Failed to initialize JWT keys", exception);

			throw new RuntimeException(
				"Failed to initialize JWT keys", exception);
		}
	}

	private void _initializeRsaKeys() throws Exception {
		String privateKeyStr = _jwtProperties.getPrivateKey();
		String publicKeyStr = _jwtProperties.getPublicKey();

		if ((privateKeyStr != null) && (publicKeyStr != null)) {
			PrivateKey privateKey = _parsePrivateKey(privateKeyStr);
			PublicKey publicKey = _parsePublicKey(publicKeyStr);

			keyPair = new KeyPair(publicKey, privateKey);

			signingKey = privateKey;
		}
		else {
			_generateDefaultRsaKeys();
		}
	}

	private boolean _isEmptyAfterTrim(String value) {
		String trimmed = value.trim();

		return trimmed.isEmpty();
	}

	private PrivateKey _parsePrivateKey(String privateKeyStr) throws Exception {
		String cleanKey = privateKeyStr;

		cleanKey = cleanKey.replace("-----BEGIN PRIVATE KEY-----", "");
		cleanKey = cleanKey.replace("-----END PRIVATE KEY-----", "");
		cleanKey = cleanKey.replaceAll("\\s", "");

		Base64.Decoder decoder = Base64.getDecoder();

		byte[] keyBytes = decoder.decode(cleanKey);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePrivate(spec);
	}

	private PublicKey _parsePublicKey(String publicKeyStr) throws Exception {
		String cleanKey = publicKeyStr;

		cleanKey = cleanKey.replace("-----BEGIN PUBLIC KEY-----", "");
		cleanKey = cleanKey.replace("-----END PUBLIC KEY-----", "");
		cleanKey = cleanKey.replaceAll("\\s", "");

		Base64.Decoder decoder = Base64.getDecoder();

		byte[] keyBytes = decoder.decode(cleanKey);

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePublic(spec);
	}

	private final JwtProperties _jwtProperties;
	private KeyPair keyPair;
	private Key signingKey;

}