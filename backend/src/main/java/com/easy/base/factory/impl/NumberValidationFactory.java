package com.easy.base.factory.impl;

import com.easy.base.annotation.ValidatesFieldType;
import com.easy.base.factory.ValidationFactory;
import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
@ValidatesFieldType(FieldTypes.NUMBER)
public class NumberValidationFactory implements ValidationFactory {
    private static final String BSON_TYPE = "number";
    private static final String MINIMUM = "minimum";
    private static final String MAXIMUM = "maximum";

    @Override
    public Document createValidation(FlexiTableField field) {
        Document numberDoc = new Document("bsonType", BSON_TYPE);

        if (field.getValidations() == null || field.getValidations().isEmpty()) {
            return numberDoc;
        }

        field.getValidations().forEach(validation -> {
            try {
                _applyValidation(numberDoc, validation);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("Invalid numeric value for %s validation in field '%s': %s",
                                validation.getValidationType(),
                                field.getFieldName(),
                                validation.getValue()
                        ), e
                );
            }
        });

        return numberDoc;
    }

    private void _applyValidation(Document doc, FlexiFieldValidation validation) {
        switch (validation.getValidationType()) {
            case MIN_VALUE -> doc.append(MINIMUM, _parseDoubleSafely(validation.getValue()));
            case MAX_VALUE -> doc.append(MAXIMUM, _parseDoubleSafely(validation.getValue()));
        }
    }

    @Override
    public boolean validate(FlexiTableField field, Object value, FlexiFieldValidation rule) {
        if (value == null) {
            return false;
        }

        try {
            double numericValue = _parseDoubleSafely(value.toString());
            return _validateValue(numericValue, rule);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid numeric value for field '%s': %s",
                            field.getFieldName(),
                            value
                    ), e
            );
        }
    }

    private boolean _validateValue(double numericValue, FlexiFieldValidation rule) {
        return switch (rule.getValidationType()) {
            case MIN_VALUE -> _validateMinValue(numericValue, rule.getValue());
            case MAX_VALUE -> _validateMaxValue(numericValue, rule.getValue());
            default -> true;
        };
    }

    private boolean _validateMinValue(double value, String minValue) {
        try {
            return value >= _parseDoubleSafely(minValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid minimum value: " + minValue, e);
        }
    }

    private boolean _validateMaxValue(double value, String maxValue) {
        try {
            return value <= _parseDoubleSafely(maxValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid maximum value: " + maxValue, e);
        }
    }

    private static double _parseDoubleSafely(String value) {
        try {
            double result = Double.parseDouble(value);
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                throw new IllegalArgumentException(
                        "Value must be a finite number: " + value
                );
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + value, e);
        }
    }
}
