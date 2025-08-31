/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.data.engine.entity;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data record entity representing dynamic data
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