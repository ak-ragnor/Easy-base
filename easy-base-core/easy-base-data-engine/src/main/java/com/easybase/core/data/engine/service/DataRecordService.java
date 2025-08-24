package com.easybase.core.data.engine.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.repository.DataRecordRepository;
import com.easybase.core.data.engine.service.validator.DataRecordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataRecordService {

	public DataRecord createRecord(UUID tenantId, String table,
			Map<String, Object> data) {
		_dataRecordValidator.validate(tenantId, table, data);
		return _dataRecordRepository.insert(tenantId, table, UUID.randomUUID(),
				data);
	}

	public void deleteRecord(UUID tenantId, String table, UUID id) {
		_dataRecordRepository.delete(tenantId, table, id);
	}

	public DataRecord getRecord(UUID tenantId, String table, UUID id) {
		return _dataRecordRepository.findById(tenantId, table, id).orElseThrow(
				() -> new ResourceNotFoundException("Record", "id", id));
	}

	public List<DataRecord> getRecords(UUID tenantId, String table) {
		return _dataRecordRepository.findAll(tenantId, table);
	}

	public DataRecord updateRecord(UUID tenantId, String table, UUID id,
			Map<String, Object> data) {
		_dataRecordValidator.validate(tenantId, table, data);
		return _dataRecordRepository.update(tenantId, table, id, data);
	}

	private final DataRecordRepository _dataRecordRepository;

	private final DataRecordValidator _dataRecordValidator;
}
