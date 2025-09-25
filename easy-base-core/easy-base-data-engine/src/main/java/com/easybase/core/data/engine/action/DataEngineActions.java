/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.action;

/**
 * Action constants for data engine permissions.
 *
 * @author Akhash R
 */
public final class DataEngineActions {

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

	public static final String SCHEMA_CREATE = "SCHEMA:CREATE";

	public static final String SCHEMA_DELETE = "SCHEMA:DELETE";

	public static final String SCHEMA_UPDATE = "SCHEMA:UPDATE";

	public static final String SCHEMA_VIEW = "SCHEMA:VIEW";

	private DataEngineActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}