/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

import jakarta.persistence.*;

import jakarta.validation.constraints.Size;

import java.util.List;

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
	name = "eb_folder",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "tenant_id"})}
)
public class FolderEntry extends SingleKeyBaseEntity {

	@Column(name = "description")
	@Size(max = 100)
	private String description;

	@OneToMany(
		cascade = CascadeType.ALL, fetch = FetchType.LAZY,
		mappedBy = "folderEntry"
	)
	private List<FileEntry> fileEntries;

	@Column(name = "name")
	@Size(max = 100)
	private String name;

	@Column(name = "parent_folder_id")
	private long parentFolderId;

	@Column(name = "path_tree")
	@Size(max = 255)
	private String pathTree;

	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tenant tenant;

}