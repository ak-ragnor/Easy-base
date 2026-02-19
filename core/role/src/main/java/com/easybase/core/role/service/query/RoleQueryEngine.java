/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.query;

import com.easybase.core.role.domain.entity.Role;
import com.easybase.core.role.infrastructure.presistence.repository.RoleRepository;
import com.easybase.core.search.jpa.AbstractJpaQueryEngine;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.hibernate.query.criteria.HibernateCriteriaBuilder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class RoleQueryEngine extends AbstractJpaQueryEngine<Role> {

	@Override
	protected Specification<Role> baseSpec(UUID tenantId) {
		return (Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
			cb.and(
				cb.or(
					cb.isNull(root.get("tenantId")),
					cb.equal(root.get("tenantId"), tenantId)),
				cb.equal(root.get("deleted"), false));
	}

	@Override
	protected Map<String, Class<?>> getFieldTypes() {
		return _fieldTypes;
	}

	@Override
	protected JpaSpecificationExecutor<Role> getRepository() {
		return _roleRepository;
	}

	@Override
	protected Specification<Role> searchSpec(String searchTerm) {
		return (Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder)cb;

			return cb.isTrue(
				hcb.sql(
					"search_vector @@ plainto_tsquery('simple', ?)",
					Boolean.class, cb.literal(searchTerm)));
		};
	}

	private static final Map<String, Class<?>> _fieldTypes = Map.of(
		"active", Boolean.class, "description", String.class, "id", UUID.class,
		"name", String.class, "system", Boolean.class, "tenantId", UUID.class);

	private final RoleRepository _roleRepository;

}