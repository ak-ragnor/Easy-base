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

package com.easybase.core.user.entity;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.tenant.entity.Tenant;

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

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
	name = "eb_users",
	uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)
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

}