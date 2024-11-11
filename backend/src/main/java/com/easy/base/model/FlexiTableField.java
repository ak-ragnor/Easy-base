package com.easy.base.model;

import com.easy.base.model.enums.FieldTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlexiTableField {

    private String fieldName;
    private FieldTypes fieldType;
    private boolean indexed;
    private boolean required;
    private List<FlexiFieldValidation> validations;
}
