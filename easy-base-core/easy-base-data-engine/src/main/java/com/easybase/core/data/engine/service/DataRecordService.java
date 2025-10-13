/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service;

import com.easybase.core.data.engine.entity.DataRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * External-facing service interface for data record operations.
 * Performs permission checks before delegating to DataRecordLocalService.
 * Never performs persistence directly - always delegates to DataRecordLocalService.
 *
 * @author Akhash R
 */
public interface DataRecordService {

	/**
	 * Creates a new record.
	 * Requires RECORD:CREATE permission.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param data the record data
	 * @return the created record
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	DataRecord createRecord(
		UUID tenantId, String table, Map<String, Object> data);

	/**
	 * Deletes a record.
	 * Requires RECORD:DELETE permission.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	void deleteRecord(UUID tenantId, String table, UUID id);

	/**
	 * Gets a record by ID.
	 * Requires RECORD:VIEW permission.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 * @return the record
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	DataRecord getRecord(UUID tenantId, String table, UUID id);

	/**
	 * Gets all records for a table.
	 * Requires RECORD:LIST permission.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @return list of records
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	List<DataRecord> getRecords(UUID tenantId, String table);

	/**
	 * Updates a record.
	 * Requires RECORD:UPDATE permission.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 * @param data the updated data
	 * @return the updated record
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	DataRecord updateRecord(
		UUID tenantId, String table, UUID id, Map<String, Object> data);

}