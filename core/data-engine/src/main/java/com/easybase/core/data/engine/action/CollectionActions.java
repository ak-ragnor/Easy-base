/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.action;

import com.easybase.infrastructure.auth.annotation.ActionDefinition;

/**
 * Action constants for data engine permissions.
 *
 * @author Akhash R
 */
@ActionDefinition(resourceType = "COLLECTION")
public final class CollectionActions {

	public static final String COLLECTION_CREATE = "COLLECTION:CREATE";

	public static final String COLLECTION_DELETE = "COLLECTION:DELETE";

	public static final String COLLECTION_LIST = "COLLECTION:LIST";

	public static final String COLLECTION_UPDATE = "COLLECTION:UPDATE";

	public static final String COLLECTION_VIEW = "COLLECTION:VIEW";

	public static final String RECORD_CREATE = "RECORD:CREATE";

	public static final String RECORD_DELETE = "RECORD:DELETE";

	public static final String RECORD_LIST = "RECORD:LIST";

	public static final String RECORD_UPDATE = "RECORD:UPDATE";

	public static final String RECORD_VIEW = "RECORD:VIEW";

	private CollectionActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}