package com.easyBase.common.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

/**
 * Custom JSON Message Converter
 * Configures Jackson for proper JSON serialization/deserialization
 * Placed in common module to avoid circular dependencies
 */
@Component
public class CustomJsonConverter extends MappingJackson2HttpMessageConverter {

    public CustomJsonConverter() {
        super();
        customizeObjectMapper();
    }

    public CustomJsonConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        customizeObjectMapper();
    }

    private void customizeObjectMapper() {
        ObjectMapper objectMapper = getObjectMapper();

        // Register Java Time module for proper date/time handling
        objectMapper.registerModule(new JavaTimeModule());

        // Configure date formatting
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Pretty print in development
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Don't fail on unknown properties
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Include non-null values only
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
    }
}