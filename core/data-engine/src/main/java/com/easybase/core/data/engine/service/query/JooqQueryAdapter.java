/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.query;

import com.easybase.core.data.engine.domain.entity.DataRecord;
import com.easybase.core.search.adapter.QueryAdapter;
import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class JooqQueryAdapter implements QueryAdapter<DataRecord> {

	@Override
	@Transactional(readOnly = true)
	public QueryResult<DataRecord> execute(SearchContext context) {
		return _jooqQueryEngine.execute(context);
	}

	@Override
	public String getEntityType() {
		return "records";
	}

	private final JooqQueryEngine _jooqQueryEngine;

}