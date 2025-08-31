/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;

import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {

	@PostConstruct
	public void initializeDatabaseExtensions() {
		_dslContext.execute("CREATE EXTENSION IF NOT EXISTS \"pgcrypto\";");
	}

	private final DSLContext _dslContext;

}