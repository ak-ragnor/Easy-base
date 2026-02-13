/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.entity;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data record entity representing dynamic data
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
public class DataRecord {

	@SuppressWarnings("unused")
	private LocalDateTime createdAt;

	@SuppressWarnings("unused")
	private Map<String, Object> data;

	@SuppressWarnings("unused")
	private UUID id;

	@SuppressWarnings("unused")
	private LocalDateTime updatedAt;

}