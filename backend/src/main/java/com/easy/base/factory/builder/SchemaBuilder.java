package com.easy.base.factory.builder;

import com.easy.base.factory.ValidationFactory;
import com.easy.base.factory.selector.ValidationFactorySelector;
import com.easy.base.model.FlexiTableField;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SchemaBuilder {

    private final ValidationFactorySelector validationFactorySelector;
    private final Document schema;
    private final Document properties;
    private final List<String> requiredFields;

    @Autowired
    public SchemaBuilder(ValidationFactorySelector validationFactorySelector) {
        this.validationFactorySelector = validationFactorySelector;
        this.schema = new Document();
        this.properties = new Document();
        this.requiredFields = new ArrayList<>(Arrays.asList("_id", "createdDate", "modifiedDate", "authorId"));
        _addDefaultFields();
    }

    public SchemaBuilder withFields(List<FlexiTableField> fields) {
        _addDynamicFields(fields);
        return this;
    }

    public Document build() {
        schema.append("properties", properties).append("required", requiredFields);
        return new Document("$jsonSchema", schema);
    }

    private void _addDefaultFields() {
        properties.append("_id", new Document("bsonType", "objectId"))
                .append("createdDate", new Document("bsonType", "date"))
                .append("modifiedDate", new Document("bsonType", "date"))
                .append("authorId", new Document("bsonType", "objectId"));
    }

    private void _addDynamicFields(List<FlexiTableField> fields) {
        fields.forEach(field -> {
            ValidationFactory validationFactory = validationFactorySelector.getFactory(field);
            Document fieldDocument = validationFactory.createValidation(field);
            properties.append(field.getFieldName(), fieldDocument);

            if (field.isRequired()) {
                requiredFields.add(field.getFieldName());
            }
        });
    }
}


