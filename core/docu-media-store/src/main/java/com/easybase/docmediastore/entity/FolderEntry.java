/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Saura
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
        name="eb_folder",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name","tenant_id"})}
)
public class FolderEntry extends SingleKeyBaseEntity {
    @OneToMany(
            cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "folderEntry"
    )
    private List<FileEntry> fileEntries;

    @Column(name = "name")
    @Size(max = 100)
    private String name;

    @Column(name = "description")
    @Size(max = 100)
    private String description;

    @Column(name = "path_tree")
    @Size(max = 255)
    private String pathTree;

    @Column(name="parent_folder_id")
    private long parentFolderId;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

}