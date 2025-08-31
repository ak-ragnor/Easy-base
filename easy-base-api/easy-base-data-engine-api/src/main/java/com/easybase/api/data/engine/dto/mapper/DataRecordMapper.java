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