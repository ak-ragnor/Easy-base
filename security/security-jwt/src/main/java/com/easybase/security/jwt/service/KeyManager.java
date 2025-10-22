/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.jwt.service;

import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;

/**
 * @author Akhash
 */
public interface KeyManager {

	public String getCurrentKeyId();

	public KeyPair getCurrentKeyPair();

	public PublicKey getPublicKey(String keyId);

	public Key getSigningKey();

}