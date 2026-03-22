/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.query;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.core.data.engine.domain.entity.Attribute;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.domain.entity.DataRecord;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.service.util.NamingUtils;
import com.easybase.infrastructure.search.FilterCondition;
import com.easybase.infrastructure.search.FilterNode;
import com.easybase.infrastructure.search.FilterOperator;
import com.easybase.infrastructure.search.QueryParser;
import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;
import com.easybase.infrastructure.search.SortField;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JooqQueryEngine {

	public QueryResult<DataRecord> execute(SearchContext context) {
		Collection collection = context.getAttribute("collection");

		String tableName = NamingUtils.getTableName(
			context.getTenantId(), collection.getName());

		Table<?> table = DSL.table(DSL.name(tableName));

		Condition condition = DSL.trueCondition();

		FilterNode filterNode = QueryParser.parseFilter(context.getFilter());

		if (filterNode != null) {
			_validateFilterFields(collection, filterNode);

			condition = condition.and(_buildCondition(collection, filterNode));
		}

		String search = context.getSearch();

		if ((search != null) && !search.isBlank()) {
			if (_hasTextAttributes(collection)) {
				condition = condition.and(_buildFtsSearchCondition(search));
			}
			else {
				condition = condition.and(_buildSearchCondition(search));
			}
		}

		SelectConditionStep<Record1<Integer>> countStep =
			_dslContext.selectCount(
			).from(
				table
			).where(
				condition
			);

		long totalElements = countStep.fetchOne(0, long.class);

		List<String> fields = context.getAttribute("fields");

		SelectJoinStep<Record> selectStep;

		if ((fields != null) && !fields.isEmpty()) {
			_queryFieldResolver.validateFields(
				collection, new HashSet<>(fields));

			List<Field<?>> selectFields = _buildSelectFields(fields);

			selectStep = _dslContext.select(
				selectFields
			).from(
				table
			);
		}
		else {
			selectStep = _dslContext.select(
			).from(
				table
			);
		}

		SelectConditionStep<Record> whereStep = selectStep.where(condition);

		List<SortField> sortFields = QueryParser.parseSort(context.getSort());

		List<org.jooq.SortField<?>> jooqSortFields = new ArrayList<>();

		if (!sortFields.isEmpty()) {
			for (SortField sortField : sortFields) {
				_validateSortField(collection, sortField.getField());

				jooqSortFields.add(_buildSortField(collection, sortField));
			}
		}
		else {
			Field<Object> createdAtField = DSL.field("created_at");

			jooqSortFields.add(createdAtField.desc());
		}

		int pageSize = context.getSize();

		int offset = context.getPage() * pageSize;

		List<DataRecord> records;

		if ((fields != null) && !fields.isEmpty()) {
			records = whereStep.orderBy(
				jooqSortFields
			).limit(
				pageSize
			).offset(
				offset
			).fetch(
				record -> _toPartialDataRecord(record, fields)
			);
		}
		else {
			records = whereStep.orderBy(
				jooqSortFields
			).limit(
				pageSize
			).offset(
				offset
			).fetch(
				this::_toDataRecord
			);
		}

		return new QueryResult<>(
			records, context.getPage(), pageSize, totalElements);
	}

	private Condition _buildComparisonCondition(
		Collection collection, String fieldName, String value,
		String sqlOperator) {

		String pgType = _queryFieldResolver.resolvePostgresType(
			collection, fieldName);

		if (_queryFieldResolver.isSystemColumn(fieldName)) {
			String dbColumn = _queryFieldResolver.toDbColumnName(fieldName);

			return DSL.condition(
				"{0} " + sqlOperator + " {1}::" + pgType,
				DSL.field(DSL.name(dbColumn)), DSL.val(value));
		}

		return DSL.condition(
			"(data->>{0})::" + pgType + " " + sqlOperator + " {1}::" + pgType,
			DSL.inline(fieldName), DSL.val(value));
	}

	private Condition _buildCondition(Collection collection, FilterNode node) {
		if (node.isLeaf()) {
			return _buildLeafCondition(collection, node.getCondition());
		}

		List<Condition> childConditions = new ArrayList<>();

		for (FilterNode child : node.getChildren()) {
			childConditions.add(_buildCondition(collection, child));
		}

		if (node.getLogic() == FilterNode.LogicOperator.AND) {
			Condition combined = DSL.trueCondition();

			for (Condition child : childConditions) {
				combined = combined.and(child);
			}

			return combined;
		}

		Condition combined = DSL.falseCondition();

		for (Condition child : childConditions) {
			combined = combined.or(child);
		}

		return combined;
	}

	private Condition _buildFtsSearchCondition(String searchTerm) {
		return DSL.condition(
			"search_vector @@ plainto_tsquery('simple', {0})",
			DSL.val(searchTerm));
	}

	private Condition _buildLeafCondition(
		Collection collection, FilterCondition condition) {

		String fieldName = condition.getField();
		FilterOperator operator = condition.getOperator();
		String value = condition.getValue();

		Field<String> fieldRef = _getFieldReference(fieldName);

		switch (operator) {
			case EQ:
				if (value == null) {
					return fieldRef.isNull();
				}

				return fieldRef.eq(value);

			case NE:
				if (value == null) {
					return fieldRef.isNotNull();
				}

				return fieldRef.ne(value);

			case GT:
				return _buildComparisonCondition(
					collection, fieldName, value, ">");

			case GE:
				return _buildComparisonCondition(
					collection, fieldName, value, ">=");

			case LT:
				return _buildComparisonCondition(
					collection, fieldName, value, "<");

			case LE:
				return _buildComparisonCondition(
					collection, fieldName, value, "<=");

			case CONTAINS:
				return fieldRef.likeIgnoreCase("%" + value + "%");

			default:
				throw new InvalidRequestException(
					"Unsupported operator: " + operator);
		}
	}

	private Condition _buildSearchCondition(String searchTerm) {
		return DSL.condition(
			"data::text ILIKE {0}", DSL.val("%" + searchTerm + "%"));
	}

	private List<Field<?>> _buildSelectFields(List<String> fieldNames) {
		List<Field<?>> fields = new ArrayList<>();

		fields.add(DSL.field(DSL.name("id")));

		boolean hasCreatedAt = false;
		boolean hasUpdatedAt = false;

		for (String fieldName : fieldNames) {
			if (fieldName.equals("id")) {
				continue;
			}

			if (fieldName.equals("createdAt")) {
				fields.add(DSL.field(DSL.name("created_at")));
				hasCreatedAt = true;
			}
			else if (fieldName.equals("updatedAt")) {
				fields.add(DSL.field(DSL.name("updated_at")));
				hasUpdatedAt = true;
			}
			else {
				Field<String> dataField = DSL.field(
					"data->>{0}", String.class, DSL.inline(fieldName));

				fields.add(dataField.as(fieldName));
			}
		}

		if (!hasCreatedAt) {
			fields.add(DSL.field(DSL.name("created_at")));
		}

		if (!hasUpdatedAt) {
			fields.add(DSL.field(DSL.name("updated_at")));
		}

		return fields;
	}

	private org.jooq.SortField<?> _buildSortField(
		Collection collection, SortField sortField) {

		String fieldName = sortField.getField();

		if (_queryFieldResolver.isSystemColumn(fieldName)) {
			String dbColumn = _queryFieldResolver.toDbColumnName(fieldName);

			Field<Object> field = DSL.field(DSL.name(dbColumn));

			if (sortField.isDescending()) {
				return field.desc();
			}

			return field.asc();
		}

		String pgType = _queryFieldResolver.resolvePostgresType(
			collection, fieldName);

		Field<Object> field = DSL.field(
			"(data->>{0})::" + pgType, DSL.inline(fieldName));

		if (sortField.isDescending()) {
			return field.desc();
		}

		return field.asc();
	}

	private void _collectFieldNames(FilterNode node, Set<String> fieldNames) {
		if (node.isLeaf()) {
			FilterCondition condition = node.getCondition();

			fieldNames.add(condition.getField());

			return;
		}

		if (node.getChildren() != null) {
			for (FilterNode child : node.getChildren()) {
				_collectFieldNames(child, fieldNames);
			}
		}
	}

	private Field<String> _getFieldReference(String fieldName) {
		if (_queryFieldResolver.isSystemColumn(fieldName)) {
			String dbColumn = _queryFieldResolver.toDbColumnName(fieldName);

			return DSL.field(DSL.name(dbColumn), String.class);
		}

		return DSL.field("data->>{0}", String.class, DSL.inline(fieldName));
	}

	private boolean _hasTextAttributes(Collection collection) {
		if (collection.getAttributes() == null) {
			return false;
		}

		for (Attribute attr : collection.getAttributes()) {
			if (attr.getDataType() == AttributeType.STRING) {
				return true;
			}
		}

		return false;
	}

	private DataRecord _toDataRecord(Record record) {
		if (record == null) {
			return null;
		}

		UUID id = record.get("id", UUID.class);

		Timestamp createdTs = record.get("created_at", Timestamp.class);
		Timestamp updatedTs = record.get("updated_at", Timestamp.class);

		LocalDateTime createdAt = null;

		if (createdTs != null) {
			createdAt = createdTs.toLocalDateTime();
		}

		LocalDateTime updatedAt = null;

		if (updatedTs != null) {
			updatedAt = updatedTs.toLocalDateTime();
		}

		String json = record.get("data", String.class);
		Map<String, Object> data = null;

		if (json != null) {
			try {
				data = _objectMapper.readValue(
					json,
					new TypeReference<Map<String, Object>>() {
					});
			}
			catch (Exception exception) {
				throw new RuntimeException(
					"Failed to parse JSONB column 'data'", exception);
			}
		}

		return new DataRecord(createdAt, data, id, updatedAt);
	}

	private DataRecord _toPartialDataRecord(
		Record record, List<String> requestedFields) {

		if (record == null) {
			return null;
		}

		UUID id = record.get("id", UUID.class);

		Timestamp createdTs = record.get("created_at", Timestamp.class);
		Timestamp updatedTs = record.get("updated_at", Timestamp.class);

		LocalDateTime createdAt = null;

		if (createdTs != null) {
			createdAt = createdTs.toLocalDateTime();
		}

		LocalDateTime updatedAt = null;

		if (updatedTs != null) {
			updatedAt = updatedTs.toLocalDateTime();
		}

		Map<String, Object> data = new LinkedHashMap<>();

		for (String fieldName : requestedFields) {
			if (fieldName.equals("id") || fieldName.equals("createdAt") ||
				fieldName.equals("updatedAt")) {

				continue;
			}

			Object value = record.get(fieldName);

			if (value != null) {
				data.put(fieldName, value);
			}
		}

		return new DataRecord(createdAt, data, id, updatedAt);
	}

	private void _validateFilterFields(Collection collection, FilterNode node) {
		Set<String> fieldNames = new HashSet<>();

		_collectFieldNames(node, fieldNames);

		_queryFieldResolver.validateFields(collection, fieldNames);
	}

	private void _validateSortField(Collection collection, String fieldName) {
		_queryFieldResolver.validateFields(collection, Set.of(fieldName));
	}

	private final DSLContext _dslContext;
	private final ObjectMapper _objectMapper;
	private final QueryFieldResolver _queryFieldResolver;

}