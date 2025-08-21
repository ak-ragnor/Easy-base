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

	private final DataRecordRepository _dataRecordRepository;
	private final DataRecordValidator _dataRecordValidator;

	public DataRecord insert(UUID tenantId, String table,
			Map<String, Object> data) {
		_dataRecordValidator.validate(tenantId, table, data);
		return _dataRecordRepository.insert(tenantId, table, UUID.randomUUID(),
				data);
	}

	public DataRecord findById(UUID tenantId, String table, UUID id) {
		return _dataRecordRepository.findById(tenantId, table, id).orElseThrow(
				() -> new ResourceNotFoundException("Record", "id", id));
	}

	public List<DataRecord> findAll(UUID tenantId, String table) {
		return _dataRecordRepository.findAll(tenantId, table);
	}

	public DataRecord update(UUID tenantId, String table, UUID id,
			Map<String, Object> data) {
		_dataRecordValidator.validate(tenantId, table, data);
		return _dataRecordRepository.update(tenantId, table, id, data);
	}

	public void delete(UUID tenantId, String table, UUID id) {
		_dataRecordRepository.delete(tenantId, table, id);
	}
}
