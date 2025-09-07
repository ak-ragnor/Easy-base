/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.system.entity;

import com.easybase.infrastructure.data.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Akhash R
 */
@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "eb_system_info")
public class SystemInfo extends BaseEntity {

	@Column(name = "app_version", nullable = false)
	private String appVersion;

	@Column(name = "db_version", nullable = false)
	private String dbVersion;

	@Column(name = "status", nullable = false)
	private String status;

}