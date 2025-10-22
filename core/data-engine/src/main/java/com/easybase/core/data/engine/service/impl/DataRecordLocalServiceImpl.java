/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.impl;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.repository.DataRecordRepository;
import com.easybase.core.data.engine.service.DataRecordLocalService;
import com.easybase.core.data.engine.service.validator.DataRecordValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DataRecordLocalService}.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class DataRecordLocalServiceImpl implements DataRecordLocalService {

	public DataRecord createRecord(
		UUID tenantId, String table, Map<String, Object> data) {

		_dataRecordValidator.validate(tenantId, table, data);

		return _dataRecordRepository.insert(
			tenantId, table, UUID.randomUUID(), data);
	}

	public void deleteRecord(UUID tenantId, String table, UUID id) {
		_dataRecordRepository.delete(tenantId, table, id);
	}

	public DataRecord getRecord(UUID tenantId, String table, UUID id) {
		Optional<DataRecord> recordOptional = _dataRecordRepository.findById(
			tenantId, table, id);

		if (recordOptional.isEmpty()) {
			throw new ResourceNotFoundException("Record", "id", id);
		}

		return recordOptional.get();
	}

	public List<DataRecord> getRecords(UUID tenantId, String table) {
		return _dataRecordRepository.findAll(tenantId, table);
	}

	public DataRecord updateRecord(
		UUID tenantId, String table, UUID id, Map<String, Object> data) {

		_dataRecordValidator.validate(tenantId, table, data);

		return _dataRecordRepository.update(tenantId, table, id, data);
	}

	private final DataRecordRepository _dataRecordRepository;
	private final DataRecordValidator _dataRecordValidator;

}