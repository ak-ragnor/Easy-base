package com.easy.base.flexi.factory.impl;

import com.easy.base.annotation.ValidatesFieldType;
import com.easy.base.flexi.factory.ValidationFactory;
import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
@ValidatesFieldType(FieldTypes.DATE)
public class DateValidationFactory implements ValidationFactory {
    @Override
    public Document createValidation(FlexiTableField field) {
        return new Document("bsonType", "date");
    }

    @Override
    public boolean validate(FlexiTableField field, Object value, FlexiFieldValidation rule) {
        return true;
    }
}
