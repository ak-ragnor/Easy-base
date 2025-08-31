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