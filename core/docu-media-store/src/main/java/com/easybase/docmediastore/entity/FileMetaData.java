/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Saura
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
	name = "eb_file_metadata",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "tenant_id"})}
)
public class FileMetaData extends SingleKeyBaseEntity {

	@Column(name = "file_id")
	private long fileId;

	@Column(columnDefinition = "jsonb", name = "metadata")
	private JsonNode metadata;

	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tenant tenant;

}