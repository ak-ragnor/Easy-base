/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.data.engine.service;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.repository.DataRecordRepository;
import com.easybase.core.data.engine.service.validator.DataRecordValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DataRecordService {

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