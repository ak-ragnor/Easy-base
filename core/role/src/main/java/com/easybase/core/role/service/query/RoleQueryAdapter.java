/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.query;

import com.easybase.core.role.domain.entity.Role;
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
public class RoleQueryAdapter implements QueryAdapter<Role> {

	@Override
	@Transactional(readOnly = true)
	public QueryResult<Role> execute(SearchContext context) {
		return _roleQueryEngine.execute(context);
	}

	@Override
	public String getEntityType() {
		return "role";
	}

	private final RoleQueryEngine _roleQueryEngine;

}