package com.easy.base.model;

import com.easy.base.model.enums.ValidatingTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlexiFieldValidation {

    private ValidatingTypes validationType;  // e.g., "Required", "MaxLength", "Pattern"
    private String value;           // value of validation
}
