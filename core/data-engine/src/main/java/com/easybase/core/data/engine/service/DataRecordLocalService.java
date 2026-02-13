/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service;

import com.easybase.core.data.engine.domain.entity.DataRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Local service interface for data record business logic and data operations.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of DataRecordService.
 *
 * @author Akhash R
 */
public interface DataRecordLocalService {

	/**
	 * Creates a new record.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param data the record data
	 * @return the created record
	 */
	public DataRecord createRecord(
		UUID tenantId, String table, Map<String, Object> data);

	/**
	 * Deletes a record.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 */
	public void deleteRecord(UUID tenantId, String table, UUID id);

	/**
	 * Gets a record by ID.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 * @return the record
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public DataRecord getRecord(UUID tenantId, String table, UUID id);

	/**
	 * Gets all records for a table.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @return list of records
	 */
	public List<DataRecord> getRecords(UUID tenantId, String table);

	/**
	 * Updates a record.
	 *
	 * @param tenantId the tenant ID
	 * @param table the table name
	 * @param id the record ID
	 * @param data the updated data
	 * @return the updated record
	 */
	public DataRecord updateRecord(
		UUID tenantId, String table, UUID id, Map<String, Object> data);

}