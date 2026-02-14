/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.auth.service.constants.ResourceActionConstants;
import com.easybase.core.auth.service.util.ActionKeyUtil;
import com.easybase.core.data.engine.domain.entity.DataRecord;
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
@RequiredArgsConstructor
@Service
public class DataRecordServiceImpl implements DataRecordService {

	@Override
	public DataRecord createRecord(
		UUID tenantId, String table, Map<String, Object> data) {

		_permissionChecker.check(
			ActionKeyUtil.getActionKey(table, ResourceActionConstants.CREATE));

		return _dataRecordLocalService.createRecord(tenantId, table, data);
	}

	@Override
	public void deleteRecord(UUID tenantId, String table, UUID id) {
		_permissionChecker.check(
			ActionKeyUtil.getActionKey(table, ResourceActionConstants.DELETE));

		_dataRecordLocalService.deleteRecord(tenantId, table, id);
	}

	@Override
	public DataRecord getRecord(UUID tenantId, String table, UUID id) {
		_permissionChecker.check(
			ActionKeyUtil.getActionKey(table, ResourceActionConstants.READ));

		return _dataRecordLocalService.getRecord(tenantId, table, id);
	}

	@Override
	public List<DataRecord> getRecords(UUID tenantId, String table) {
		_permissionChecker.check(
			ActionKeyUtil.getActionKey(table, ResourceActionConstants.READ));

		return _dataRecordLocalService.getRecords(tenantId, table);
	}

	@Override
	public DataRecord updateRecord(
		UUID tenantId, String table, UUID id, Map<String, Object> data) {

		_permissionChecker.check(
			ActionKeyUtil.getActionKey(table, ResourceActionConstants.UPDATE));

		return _dataRecordLocalService.updateRecord(tenantId, table, id, data);
	}

	private final DataRecordLocalService _dataRecordLocalService;
	private final PermissionChecker _permissionChecker;

}