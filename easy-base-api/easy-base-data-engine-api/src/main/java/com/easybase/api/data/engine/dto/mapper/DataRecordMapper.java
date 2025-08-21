package com.easybase.api.data.engine.dto.mapper;

import org.springframework.stereotype.Component;

import com.easybase.api.data.engine.dto.DataRecordDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.DataRecord;

@Component
public class DataRecordMapper implements BaseMapper<DataRecord, DataRecordDto> {

	@Override
	public DataRecordDto toDto(DataRecord record) {
		if (record == null) {
			return null;
		}

		DataRecordDto dto = new DataRecordDto();
		dto.setId(record.id());
		dto.setData(record.data());
		dto.setCreatedAt(record.createdAt());
		dto.setUpdatedAt(record.updatedAt());
		return dto;
	}

	@Override
	public DataRecord toEntity(DataRecordDto dto) {
		if (dto == null) {
			return null;
		}

		return new DataRecord(dto.getId(), dto.getData(), dto.getCreatedAt(),
				dto.getUpdatedAt());
	}
}
