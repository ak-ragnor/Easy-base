package com.easy.base.factory.selector;

import com.easy.base.annotation.ValidatesFieldType;
import com.easy.base.factory.ValidationFactory;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.enums.FieldTypes;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;


@Component
public class ValidationFactorySelector {
    private final Map<FieldTypes, ValidationFactory> factoryMap = new EnumMap<>(FieldTypes.class);
    private final ApplicationContext applicationContext;

    public ValidationFactorySelector(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void initializeFactories() {
        // Get all ValidationFactory beans and map them to their corresponding field types
        applicationContext.getBeansOfType(ValidationFactory.class)
                .values()
                .forEach(factory -> {
                    ValidatesFieldType annotation = factory.getClass().getAnnotation(ValidatesFieldType.class);
                    if (annotation != null) {
                        factoryMap.put(annotation.value(), factory);
                    }
                });
    }

    public ValidationFactory getFactory(FlexiTableField field) {
        return Optional.ofNullable(factoryMap.get(field.getFieldType()))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "No validation factory found for field type: " + field.getFieldType()));
    }
}