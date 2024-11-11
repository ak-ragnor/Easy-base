package com.easy.base.factory.impl;

import com.easy.base.annotation.ValidatesFieldType;
import com.easy.base.factory.ValidationFactory;
import com.easy.base.model.ChoiceList;
import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;
import com.easy.base.model.enums.ValidatingTypes;
import com.easy.base.repository.ChoiceListRepository;
import lombok.Builder;
import lombok.Getter;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ValidatesFieldType(FieldTypes.CHOICE)
public class ChoiceValidationFactory implements ValidationFactory {
    private final ChoiceListRepository choiceListRepository;

    public ChoiceValidationFactory(ChoiceListRepository choiceListRepository) {
        this.choiceListRepository = choiceListRepository;
    }

    @Override
    public Document createValidation(FlexiTableField field) {
        ValidationConfig config = _parseValidations(field.getValidations());
        ChoiceList choiceList = _getChoiceList(config.getChoiceId());

        return config.isMultipleSelection() ?
                _createMultipleSelectionValidation(choiceList, config.getMaxChoices()) :
                _createSingleSelectionValidation(choiceList);
    }

    @Override
    public boolean validate(FlexiTableField field, Object value, FlexiFieldValidation rule) {
        ChoiceList choiceList = _getChoiceList(rule.getValue());
        List<String> validOptions = choiceList.getOptions();

        return switch (rule.getValidationType()) {
            case SINGLE_SELECTION -> _validateSingleSelection(value, validOptions);
            case MULTIPLE_SELECTION -> _validateMultipleSelection(value, validOptions);
            case MAX_CHOICES -> _validateMaxChoices(value, Integer.parseInt(rule.getValue()));
            default -> true;
        };
    }

    private ValidationConfig _parseValidations(List<FlexiFieldValidation> validations) {
        if (validations == null || validations.isEmpty()) {
            throw new IllegalArgumentException("Validations are required for the Choice field");
        }

        ValidationConfig.ValidationConfigBuilder configBuilder = ValidationConfig.builder();

        Map<ValidatingTypes, FlexiFieldValidation> validationMap = validations.stream()
                .collect(Collectors.toMap(FlexiFieldValidation::getValidationType, Function.identity()));

        // Check for conflicting selections
        if (validationMap.containsKey(ValidatingTypes.SINGLE_SELECTION) &&
                validationMap.containsKey(ValidatingTypes.MULTIPLE_SELECTION)) {
            throw new IllegalArgumentException("Cannot set both SINGLE_SELECTION and MULTIPLE_SELECTION");
        }

        // Parse single selection
        Optional.ofNullable(validationMap.get(ValidatingTypes.SINGLE_SELECTION))
                .ifPresent(v -> configBuilder
                        .choiceId(v.getValue())
                        .multipleSelection(false));

        // Parse multiple selection
        Optional.ofNullable(validationMap.get(ValidatingTypes.MULTIPLE_SELECTION))
                .ifPresent(v -> configBuilder
                        .choiceId(v.getValue())
                        .multipleSelection(true));

        // Parse max choices for multiple selection
        if (configBuilder.build().isMultipleSelection()) {
            Optional.ofNullable(validationMap.get(ValidatingTypes.MAX_CHOICES))
                    .ifPresent(v -> configBuilder.maxChoices(Integer.parseInt(v.getValue())));
        }

        ValidationConfig config = configBuilder.build();

        if (config.getChoiceId() == null) {
            throw new IllegalArgumentException("Choice list ID is required for choice fields");
        }

        return config;
    }

    private Document _createSingleSelectionValidation(ChoiceList choiceList) {
        return new Document("bsonType", "string")
                .append("enum", choiceList.getOptions());
    }

    private Document _createMultipleSelectionValidation(ChoiceList choiceList, Integer maxChoices) {
        Document validation = new Document("bsonType", "array")
                .append("items", new Document("bsonType", "string")
                        .append("enum", choiceList.getOptions()));

        Optional.ofNullable(maxChoices)
                .ifPresent(max -> validation.append("maxItems", max));

        return validation;
    }

    private ChoiceList _getChoiceList(String choiceId) {
        return choiceListRepository.findById(choiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid choice list ID: " + choiceId));
    }

    private boolean _validateSingleSelection(Object value, List<String> validOptions) {
        return value != null && validOptions.contains(value.toString());
    }

    private boolean _validateMultipleSelection(Object value, List<String> validOptions) {
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalArgumentException("Value should be a list for multiple selection");
        }
        return listValue.stream()
                .map(Object::toString)
                .allMatch(validOptions::contains);
    }

    private boolean _validateMaxChoices(Object value, int maxChoices) {
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalArgumentException("Value should be a list for multiple selection");
        }
        return listValue.size() <= maxChoices;
    }

    @Getter
    @lombok.Builder
    private static class ValidationConfig {
        private final String choiceId;
        @Builder.Default
        private final boolean multipleSelection = false;  // Renamed from isMultipleSelection
        private final Integer maxChoices;
    }
}
