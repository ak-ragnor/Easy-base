/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;
import jakarta.persistence.*;
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
        name="eb_file",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name","tenant_id"})}
)
public class FileEntry extends SingleKeyBaseEntity {

    @Column(name = "title")
    @Size(max = 255)
    private String title;

    @Column(name = "file_name")
    @Size(max = 255)
    private String fileName;

    @Column(name = "path_tree")
    @Size(max = 255)
    private String pathTree;

    @Column(name = "mime_type")
    @Size(max = 255)
    private String mimeType;

    @Column(name = "extension")
    @Size(max = 255)
    private String extension;

    @Column(name = "size")
    private long size;

    @JoinColumn(name = "folder_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private FolderEntry folderEntry;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;
}