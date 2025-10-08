/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.data.engine.action.DataEngineActions;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.service.DataRecordLocalService;
import com.easybase.core.data.engine.service.DataRecordService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DataRecordService}.
 * ALWAYS performs permission checks before delegating to DataRecordLocalService.
 * Never performs persistence directly - always delegates to DataRecordLocalService.
 *
 * <p>If permission checks are not needed, use DataRecordLocalService directly.</p>
 *
 * @author Akhash R
 */
@Service
@RequiredArgsConstructor
public class DataRecordServiceImpl implements DataRecordService {

	@Override
	public DataRecord createRecord(
		UUID tenantId, String table, Map<String, Object> data) {

		_permissionChecker.check(DataEngineActions.RECORD_CREATE);

		return _dataRecordLocalService.createRecord(tenantId, table, data);
	}

	@Override
	public void deleteRecord(UUID tenantId, String table, UUID id) {
		_permissionChecker.check(DataEngineActions.RECORD_DELETE);

		_dataRecordLocalService.deleteRecord(tenantId, table, id);
	}

	@Override
	public DataRecord getRecord(UUID tenantId, String table, UUID id) {
		_permissionChecker.check(DataEngineActions.RECORD_VIEW);

		return _dataRecordLocalService.getRecord(tenantId, table, id);
	}

	@Override
	public List<DataRecord> getRecords(UUID tenantId, String table) {
		_permissionChecker.check(DataEngineActions.RECORD_LIST);

		return _dataRecordLocalService.getRecords(tenantId, table);
	}

	@Override
	public DataRecord updateRecord(
		UUID tenantId, String table, UUID id, Map<String, Object> data) {

		_permissionChecker.check(DataEngineActions.RECORD_UPDATE);

		return _dataRecordLocalService.updateRecord(tenantId, table, id, data);
	}

	private final PermissionChecker _permissionChecker;
	private final DataRecordLocalService _dataRecordLocalService;

}
