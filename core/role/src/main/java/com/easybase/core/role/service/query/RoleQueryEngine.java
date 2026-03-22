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

import java.util.UUID;

import org.hibernate.query.criteria.HibernateCriteriaBuilder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class RoleQueryEngine extends AbstractJpaQueryEngine<Role> {

	public RoleQueryEngine(
		RoleMetadataContributor contributor, RoleRepository roleRepository) {

		super(contributor);

		_roleRepository = roleRepository;
	}

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

	private final RoleRepository _roleRepository;

}