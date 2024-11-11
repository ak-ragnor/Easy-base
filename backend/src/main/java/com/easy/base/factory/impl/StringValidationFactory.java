package com.easy.base.factory.impl;

import com.easy.base.annotation.ValidatesFieldType;
import com.easy.base.factory.ValidationFactory;
import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
@ValidatesFieldType(FieldTypes.TEXT)
public class StringValidationFactory implements ValidationFactory {
    private static final String BSON_TYPE = "string";
    private static final String MIN_LENGTH = "minLength";
    private static final String MAX_LENGTH = "maxLength";
    private static final String PATTERN = "pattern";

    @Override
    public Document createValidation(FlexiTableField field) {
        Document stringDoc = new Document("bsonType", BSON_TYPE);

        if (field.getValidations() == null || field.getValidations().isEmpty()) {
            return stringDoc;
        }

        field.getValidations().forEach(validation -> {
            try {
                _applyValidation(stringDoc, validation);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("Invalid value for %s validation: %s",
                                validation.getValidationType(),
                                validation.getValue()
                        ), e
                );
            }
        });

        return stringDoc;
    }

    private void _applyValidation(Document doc, FlexiFieldValidation validation) {
        switch (validation.getValidationType()) {
            case MIN_LENGTH -> doc.append(MIN_LENGTH, _parseIntSafely(validation.getValue()));
            case MAX_LENGTH -> doc.append(MAX_LENGTH, _parseIntSafely(validation.getValue()));
            case PATTERN -> doc.append(PATTERN, validation.getValue());
        }
    }

    @Override
    public boolean validate(FlexiTableField field, Object value, FlexiFieldValidation rule) {
        if (value == null) {
            return false;
        }

        String stringValue = value.toString();

        return switch (rule.getValidationType()) {
            case MIN_LENGTH -> _validateMinLength(stringValue, rule.getValue());
            case MAX_LENGTH -> _validateMaxLength(stringValue, rule.getValue());
            case PATTERN -> _validatePattern(stringValue, rule.getValue());
            default -> true;
        };
    }

    private boolean _validateMinLength(String value, String minLength) {
        try {
            return value.length() >= _parseIntSafely(minLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid minimum length: " + minLength, e);
        }
    }

    private boolean _validateMaxLength(String value, String maxLength) {
        try {
            return value.length() <= _parseIntSafely(maxLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid maximum length: " + maxLength, e);
        }
    }

    private boolean _validatePattern(String value, String pattern) {
        try {
            return value.matches(pattern);
        } catch (java.util.regex.PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression pattern: " + pattern, e);
        }
    }

    private static int _parseIntSafely(String value) {
        try {
            int result = Integer.parseInt(value);

            if (result < 0) {
                throw new IllegalArgumentException("Value must be non-negative: " + value);
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + value, e);
        }
    }
}

