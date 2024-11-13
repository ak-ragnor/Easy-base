package com.easy.base.controller.dto;

import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;

import java.util.List;

public record FlexiTableFieldDTO(
        String fieldName,
        FieldTypes fieldType,
        boolean indexed,
        boolean required,
        List<FlexiFieldValidationDTO> validations) {

    public FlexiTableFieldDTO(FlexiTableField a) {
        this(
                a.getFieldName(),
                a.getFieldType(),
                a.isIndexed(),
                a.isRequired(),
                a.getValidations().stream().map(FlexiFieldValidationDTO::new).toList()
        );
    }

    public FlexiTableField toFlexiTableField() {
        return FlexiTableField.builder()
                .fieldName(fieldName)
                .fieldType(fieldType)
                .indexed(indexed)
                .required(required)
                .validations(validations.stream().map(FlexiFieldValidationDTO::toFlexiFieldValidation).toList())
                .build();
    }
}

