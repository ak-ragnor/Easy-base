package com.easy.base.flexi.builder;

import com.easy.base.flexi.factory.ValidationFactory;
import com.easy.base.flexi.selector.ValidationFactorySelector;
import com.easy.base.model.FlexiTableField;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SchemaBuilder {
    private final ValidationFactorySelector validationFactorySelector;
    private final Map<String, Document> schemaProperties;
    private final List<String> requiredFields;

    @Autowired
    public SchemaBuilder(ValidationFactorySelector validationFactorySelector) {
        this.validationFactorySelector = validationFactorySelector;
        this.schemaProperties = new ConcurrentHashMap<>();
        this.requiredFields = new ArrayList<>(List.of("_id", "createdDate", "modifiedDate", "authorId"));
        _addDefaultFields();
    }

    public SchemaBuilder withFields(List<FlexiTableField> fields) {
        _addDynamicFields(fields);
        return this;
    }

    public Document build() {
        Document schema = new Document("properties", schemaProperties)
                .append("required", requiredFields);
        return new Document("$jsonSchema", schema);
    }

    private void _addDefaultFields() {
        schemaProperties.put("_id", new Document("bsonType", "objectId"));
        schemaProperties.put("createdDate", new Document("bsonType", "date"));
        schemaProperties.put("modifiedDate", new Document("bsonType", "date"));
        schemaProperties.put("authorId", new Document("bsonType", "objectId"));
    }

    private void _addDynamicFields(List<FlexiTableField> fields) {
        Map<Boolean, List<FlexiTableField>> partitionedFields = fields.stream()
                .collect(Collectors.partitioningBy(FlexiTableField::isRequired));

        partitionedFields.get(true).forEach(this::_addRequiredField);
        partitionedFields.get(false).forEach(this::_addOptionalField);
    }

    private void _addRequiredField(FlexiTableField field) {
        ValidationFactory validationFactory = validationFactorySelector.getFactory(field);
        Document fieldDocument = validationFactory.createValidation(field);
        schemaProperties.put(field.getFieldName(), fieldDocument);
        requiredFields.add(field.getFieldName());
    }

    private void _addOptionalField(FlexiTableField field) {
        ValidationFactory validationFactory = validationFactorySelector.getFactory(field);
        Document fieldDocument = validationFactory.createValidation(field);
        schemaProperties.put(field.getFieldName(), fieldDocument);
    }
}


