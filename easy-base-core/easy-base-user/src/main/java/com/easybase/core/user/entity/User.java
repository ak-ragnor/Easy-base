/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true, exclude = "userRoles")
@NoArgsConstructor
@Table(
	name = "eb_users",
	uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@ToString(exclude = "userRoles")
public class User extends BaseEntity {

	@OneToMany(
		cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user"
	)
	private List<UserCredential> credentials;

	@Column(name = "display_name")
	@Size(max = 100)
	private String displayName;

	@Column(name = "email", nullable = false, unique = true)
	@Email
	@NotBlank
	@Size(max = 255)
	private String email;

	@Column(name = "first_name")
	@Size(max = 100)
	private String firstName;

	@Column(name = "last_name")
	@Size(max = 100)
	private String lastName;

	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tenant tenant;

	@OneToMany(
		cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user",
		orphanRemoval = true
	)
	private Set<UserRole> userRoles = new HashSet<>();

}