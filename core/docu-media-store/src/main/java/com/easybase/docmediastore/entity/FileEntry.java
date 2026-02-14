/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
	name = "eb_file",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "tenant_id"})}
)
public class FileEntry extends SingleKeyBaseEntity {

	@Column(name = "extension")
	@Size(max = 255)
	private String extension;

	@Column(name = "file_name")
	@Size(max = 255)
	private String fileName;

	@JoinColumn(name = "folder_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private FolderEntry folderEntry;

	@Column(name = "mime_type")
	@Size(max = 255)
	private String mimeType;

	@Column(name = "path_tree")
	@Size(max = 255)
	private String pathTree;

	@Column(name = "size")
	private long size;

	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tenant tenant;

	@Column(name = "title")
	@Size(max = 255)
	private String title;

}