package com.easy.base.controller.dto;

import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.enums.ValidatingTypes;

public record FlexiFieldValidationDTO(
        ValidatingTypes validationType,
        String value) {

    public FlexiFieldValidationDTO(FlexiFieldValidation a) {
        this(a.getValidationType(), a.getValue());
    }

    public FlexiFieldValidation toFlexiFieldValidation() {
        return FlexiFieldValidation.builder()
                .validationType(validationType)
                .value(value)
                .build();
    }

}
