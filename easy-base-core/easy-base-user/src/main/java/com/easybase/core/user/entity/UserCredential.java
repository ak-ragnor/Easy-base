/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "eb_user_credentials")
public class UserCredential {

	@Column(columnDefinition = "jsonb", name = "credential_data")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> credentialData;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(length = 32, name = "password_algo")
	@Size(max = 32)
	private String passwordAlgo;

	@Column(name = "password_changed_at")
	private LocalDateTime passwordChangedAt;

	@Column(length = 255, name = "password_hash")
	private String passwordHash;

	@Column(name = "password_type", nullable = false)
	@NotBlank
	@Size(max = 50)
	private String passwordType;

	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private User user;

}