/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.search.jpa;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.infrastructure.search.FilterCondition;
import com.easybase.infrastructure.search.FilterNode;
import com.easybase.infrastructure.search.FilterOperator;
import com.easybase.infrastructure.search.QueryParser;
import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;
import com.easybase.infrastructure.search.SortField;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Abstract base class for JPA Specification-based query engines.
 * Handles the full query lifecycle: filter parsing, specification building,
 * sorting, pagination, and result wrapping. Subclasses only override
 * entity-specific behavior.
 *
 * @param <E> the JPA entity type
 * @author Akhash R
 */
public abstract class AbstractJpaQueryEngine<E> {

	public QueryResult<E> execute(SearchContext context) {
		Specification<E> spec = baseSpec(context.getTenantId());

		FilterNode filterNode = QueryParser.parseFilter(context.getFilter());

		if (filterNode != null) {
			spec = spec.and(_buildSpecification(filterNode));
		}

		String search = context.getSearch();

		if ((search != null) && !search.isBlank()) {
			Specification<E> searchSpec = searchSpec(search);

			if (searchSpec != null) {
				spec = spec.and(searchSpec);
			}
		}

		Sort sort = _buildSort(context.getSort());

		PageRequest pageable = PageRequest.of(
			context.getPage(), context.getSize(), sort);

		Page<E> page = getRepository().findAll(spec, pageable);

		return new QueryResult<>(
			page.getContent(), page.getNumber(), page.getSize(),
			page.getTotalElements());
	}

	protected abstract Specification<E> baseSpec(UUID tenantId);

	protected Sort defaultSort() {
		return Sort.by(Sort.Direction.DESC, "updatedAt");
	}

	protected Map<String, Class<?>> getFieldTypes() {
		return Collections.emptyMap();
	}

	protected abstract JpaSpecificationExecutor<E> getRepository();

	protected Specification<E> searchSpec(String searchTerm) {
		return null;
	}

	@SuppressWarnings("unchecked")
	private Specification<E> _buildLeafSpec(FilterCondition condition) {
		String field = condition.getField();
		FilterOperator operator = condition.getOperator();
		String value = condition.getValue();

		return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			Path<?> path = root.get(field);
			Class<?> fieldType = getFieldTypes().getOrDefault(
				field, String.class);

			switch (operator) {
				case EQ:
					if (value == null) {
						return cb.isNull(path);
					}

					return cb.equal(path, _coerce(value, fieldType));

				case NE:
					if (value == null) {
						return cb.isNotNull(path);
					}

					return cb.notEqual(path, _coerce(value, fieldType));

				case GT:
					return cb.greaterThan(
						(Path<Comparable>)path,
						(Comparable)_coerce(value, fieldType));

				case GE:
					return cb.greaterThanOrEqualTo(
						(Path<Comparable>)path,
						(Comparable)_coerce(value, fieldType));

				case LT:
					return cb.lessThan(
						(Path<Comparable>)path,
						(Comparable)_coerce(value, fieldType));

				case LE:
					return cb.lessThanOrEqualTo(
						(Path<Comparable>)path,
						(Comparable)_coerce(value, fieldType));

				case CONTAINS:
					return cb.like(
						cb.lower((Path<String>)path),
						"%" + value.toLowerCase() + "%");

				default:
					throw new InvalidRequestException(
						"Unsupported operator: " + operator);
			}
		};
	}

	private Sort _buildSort(String sortStr) {
		List<SortField> sortFields = QueryParser.parseSort(sortStr);

		if (sortFields.isEmpty()) {
			return defaultSort();
		}

		List<Sort.Order> orders = new ArrayList<>();

		for (SortField sortField : sortFields) {
			Sort.Direction direction =
				sortField.isDescending() ? Sort.Direction.DESC :
					Sort.Direction.ASC;

			orders.add(new Sort.Order(direction, sortField.getField()));
		}

		return Sort.by(orders);
	}

	private Specification<E> _buildSpecification(FilterNode node) {
		if (node.isLeaf()) {
			return _buildLeafSpec(node.getCondition());
		}

		List<Specification<E>> childSpecs = new ArrayList<>();

		for (FilterNode child : node.getChildren()) {
			childSpecs.add(_buildSpecification(child));
		}

		if (node.getLogic() == FilterNode.LogicOperator.AND) {
			Specification<E> combined = Specification.where(null);

			for (Specification<E> child : childSpecs) {
				combined = combined.and(child);
			}

			return combined;
		}

		Specification<E> combined = Specification.where(null);

		for (int i = 0; i < childSpecs.size(); i++) {
			if (i == 0) {
				combined = Specification.where(childSpecs.get(i));
			}
			else {
				combined = combined.or(childSpecs.get(i));
			}
		}

		return combined;
	}

	private Object _coerce(String value, Class<?> targetType) {
		if (value == null) {
			return null;
		}

		if (targetType == String.class) {
			return value;
		}

		if (targetType == LocalDateTime.class) {
			return LocalDateTime.parse(value);
		}

		if (targetType == UUID.class) {
			return UUID.fromString(value);
		}

		if ((targetType == Long.class) || (targetType == long.class)) {
			try {
				return Long.parseLong(value);
			}
			catch (NumberFormatException numberFormatException) {
				throw new InvalidRequestException(
					"Invalid long value: " + value, numberFormatException);
			}
		}

		if ((targetType == Integer.class) || (targetType == int.class)) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException numberFormatException) {
				throw new InvalidRequestException(
					"Invalid integer value: " + value, numberFormatException);
			}
		}

		if ((targetType == Boolean.class) || (targetType == boolean.class)) {
			return Boolean.parseBoolean(value);
		}

		return value;
	}

}