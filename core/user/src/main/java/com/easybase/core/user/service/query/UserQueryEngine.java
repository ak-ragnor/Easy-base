/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service.query;

import com.easybase.core.search.jpa.AbstractJpaQueryEngine;
import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.infrastructure.presistence.repository.UserRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;

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
public class UserQueryEngine extends AbstractJpaQueryEngine<User> {

	@Override
	protected Specification<User> baseSpec(UUID tenantId) {
		return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			Path<?> tenantPath = root.get("tenant");

			return cb.and(
				cb.equal(tenantPath.get("id"), tenantId),
				cb.equal(root.get("deleted"), false));
		};
	}

	@Override
	protected Map<String, Class<?>> getFieldTypes() {
		return _fieldTypes;
	}

	@Override
	protected JpaSpecificationExecutor<User> getRepository() {
		return _userRepository;
	}

	@Override
	protected Specification<User> searchSpec(String searchTerm) {
		return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder)cb;

			return cb.isTrue(
				hcb.sql(
					"search_vector @@ plainto_tsquery('simple', ?)",
					Boolean.class, cb.literal(searchTerm)));
		};
	}

	private static final Map<String, Class<?>> _fieldTypes = Map.of(
		"createdAt", LocalDateTime.class, "displayName", String.class, "email",
		String.class, "firstName", String.class, "id", UUID.class, "lastName",
		String.class, "updatedAt", LocalDateTime.class);

	private final UserRepository _userRepository;

}