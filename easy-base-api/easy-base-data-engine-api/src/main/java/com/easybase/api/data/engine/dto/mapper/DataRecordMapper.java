/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto.mapper;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.DataRecord;

import org.springframework.stereotype.Component;

@Component
public class DataRecordMapper implements BaseMapper<DataRecord, DataRecordDto> {

	@Override
	public DataRecordDto toDto(DataRecord record) {
		if (record == null) {
			return null;
		}

		DataRecordDto dto = new DataRecordDto();

		dto.setId(record.getId());
		dto.setData(record.getData());
		dto.setCreatedAt(record.getCreatedAt());
		dto.setUpdatedAt(record.getUpdatedAt());

		return dto;
	}

	@Override
	public DataRecord toEntity(DataRecordDto dto) {
		if (dto == null) {
			return null;
		}

		return new DataRecord(
			dto.getCreatedAt(), dto.getData(), dto.getId(), dto.getUpdatedAt());
	}

}