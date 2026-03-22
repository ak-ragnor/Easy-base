/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service.query;

import com.easybase.core.search.adapter.QueryAdapter;
import com.easybase.core.user.domain.entity.User;
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
public class UserQueryAdapter implements QueryAdapter<User> {

	@Override
	@Transactional(readOnly = true)
	public QueryResult<User> execute(SearchContext context) {
		return _userQueryEngine.execute(context);
	}

	@Override
	public String getEntityType() {
		return "user";
	}

	private final UserQueryEngine _userQueryEngine;

}