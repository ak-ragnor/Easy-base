package com.easyBase.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

/**
 * Shared beans configuration
 * Contains beans that are needed by multiple modules to avoid circular dependencies
 */
@Configuration
public class SharedBeansConfig {

    /**
     * Configuration provider registry
     * Central registry for cross-module configuration access
     */
    @Bean
    public ConfigProviderRegistry configProviderRegistry() {
        return new ConfigProviderRegistry();
    }

    /**
     * Bean needed by Spring Security but defined in Web context
     * This resolves the circular dependency issue
     */
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    /**
     * Shared ObjectMapper configuration
     * Used by both Web MVC and other modules for JSON processing
     */
    @Bean
    public ObjectMapper sharedObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(new LocalDateTimeSerializer(
                DateTimeFormatter.ISO_DATE_TIME
        ));
        objectMapper.registerModule(javaTimeModule);

        // Configure features
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    /**
     * Property sources configuration
     * Needed early in the configuration process
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(false);
        configurer.setIgnoreResourceNotFound(true);
        return configurer;
    }
}