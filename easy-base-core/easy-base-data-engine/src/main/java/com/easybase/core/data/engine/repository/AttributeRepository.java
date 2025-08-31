/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.repository;

import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

	public boolean existsByCollectionAndName(
		Collection collection, String name);

	public boolean existsByCollectionIdAndName(UUID collectionId, String name);

	public List<Attribute> findByCollection(Collection collection);

	public List<Attribute> findByCollectionAndIndexed(
		Collection collection, Boolean indexed);

	public Optional<Attribute> findByCollectionAndName(
		Collection collection, String name);

	public List<Attribute> findByCollectionId(UUID collectionId);

	public List<Attribute> findByCollectionIdAndIndexed(
		UUID collectionId, Boolean indexed);

	public Optional<Attribute> findByCollectionIdAndName(
		UUID collectionId, String name);

}