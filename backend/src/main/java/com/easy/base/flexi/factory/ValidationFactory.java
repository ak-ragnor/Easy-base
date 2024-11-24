package com.easy.base.flexi.factory;

import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public interface ValidationFactory {

    Document createValidation(FlexiTableField field);

    boolean validate(FlexiTableField field, Object value, FlexiFieldValidation rule);
}
