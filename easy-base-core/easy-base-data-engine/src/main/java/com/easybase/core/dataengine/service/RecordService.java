package com.easybase.core.dataengine.service;

import com.easybase.core.dataengine.repository.RecordRepository;
import com.easybase.core.dataengine.service.validator.RecordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository repository;
    private final RecordValidator validator;

    public UUID insert(UUID tenantId, String table, Map<String, Object> data) {
        validator.validate(tenantId, table, data);

        UUID id = UUID.randomUUID();

        repository.insert(tenantId, table, id, data);

        return id;
    }

    public Optional<Map<String, Object>> findById(UUID tenantId, String table, UUID id) {
        return repository.findById(tenantId, table, id);
    }

    public List<Map<String, Object>> findAll(UUID tenantId, String table) {
        return repository.findAll(tenantId, table);
    }

    public void update(UUID tenantId, String table, UUID id, Map<String, Object> data) {
        validator.validate(tenantId, table, data);

        repository.update(tenantId, table, id, data);
    }

    public void delete(UUID tenantId, String table, UUID id) {
        repository.delete(tenantId, table, id);
    }
}

