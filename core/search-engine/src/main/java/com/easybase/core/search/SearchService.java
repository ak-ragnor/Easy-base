/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.search;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.search.adapter.QueryAdapter;
import com.easybase.core.search.metadata.QueryMetadataContributor;
import com.easybase.infrastructure.search.FilterCondition;
import com.easybase.infrastructure.search.FilterNode;
import com.easybase.infrastructure.search.QueryParser;
import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;
import com.easybase.infrastructure.search.SortField;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class SearchService {

	public SearchService(
		List<QueryAdapter<?>> adapters,
		List<QueryMetadataContributor> contributors,
		PermissionChecker permissionChecker) {

		Stream<QueryAdapter<?>> adaptersStream = adapters.stream();

		_adapters = adaptersStream.collect(
			Collectors.toMap(QueryAdapter::getEntityType, Function.identity()));

		Stream<QueryMetadataContributor> contributorsStream =
			contributors.stream();

		_contributors = contributorsStream.collect(
			Collectors.toMap(
				QueryMetadataContributor::getEntityType, Function.identity()));

		_permissionChecker = permissionChecker;
	}

	public <T> QueryResult<T> search(SearchContext context) {
		QueryAdapter<?> adapter = _adapters.get(context.getEntityType());

		if (adapter == null) {
			throw new InvalidRequestException(
				"Unknown entity type: " + context.getEntityType());
		}

		QueryMetadataContributor contributor = _contributors.get(
			context.getEntityType());

		if (contributor != null) {
			_validateFields(context, contributor);

			if (context.isCheckPermission()) {
				String requiredPermission = contributor.getRequiredPermission();

				if (requiredPermission != null) {
					_permissionChecker.check(requiredPermission);
				}
			}
		}

		return (QueryResult<T>)adapter.execute(context);
	}

	public <E, D> QueryResult<D> search(
		SearchContext context, Function<E, D> mapper) {

		QueryResult<E> result = search(context);

		List<E> content = result.getContent();

		Stream<E> stream = content.stream();

		List<D> mapped = stream.map(
			mapper
		).toList();

		return new QueryResult<>(
			mapped, result.getPage(), result.getSize(),
			result.getTotalElements());
	}

	private void _collectFilterFields(FilterNode node, Set<String> fields) {
		if (node.isLeaf()) {
			FilterCondition condition = node.getCondition();

			fields.add(condition.getField());
		}
		else if (node.getChildren() != null) {
			for (FilterNode child : node.getChildren()) {
				_collectFilterFields(child, fields);
			}
		}
	}

	private void _validateFields(
		SearchContext context, QueryMetadataContributor contributor) {

		FilterNode filterNode = QueryParser.parseFilter(context.getFilter());

		if (filterNode != null) {
			Set<String> filterableFields = contributor.getFilterableFields();

			HashSet<String> usedFields = new HashSet<>();

			_collectFilterFields(filterNode, usedFields);

			for (String field : usedFields) {
				if (!filterableFields.contains(field)) {
					throw new InvalidRequestException(
						"Unknown or non-filterable field: " + field);
				}
			}
		}

		List<SortField> sortFields = QueryParser.parseSort(context.getSort());

		if (!sortFields.isEmpty()) {
			Set<String> sortableFields = contributor.getSortableFields();

			for (SortField sortField : sortFields) {
				if (!sortableFields.contains(sortField.getField())) {
					throw new InvalidRequestException(
						"Unknown or non-sortable field: " +
							sortField.getField());
				}
			}
		}
	}

	private final Map<String, QueryAdapter<?>> _adapters;
	private final Map<String, QueryMetadataContributor> _contributors;
	private final PermissionChecker _permissionChecker;

}