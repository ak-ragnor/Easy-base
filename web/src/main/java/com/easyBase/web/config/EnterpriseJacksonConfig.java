package com.easyBase.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Enterprise-grade Jackson configuration with full timezone support
 */
@Configuration
public class EnterpriseJacksonConfig {

    @Value("${app.timezone:UTC}")
    private String defaultTimezone;

    @Value("${app.date.format:yyyy-MM-dd}")
    private String dateFormat;

    @Value("${app.datetime.format:yyyy-MM-dd'T'HH:mm:ss}")
    private String dateTimeFormat;

    @Value("${app.datetime.zone.format:yyyy-MM-dd'T'HH:mm:ssXXX}")
    private String zonedDateTimeFormat;

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Configure date formatters
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        DateTimeFormatter zonedDateTimeFormatter = DateTimeFormatter.ofPattern(zonedDateTimeFormat);

        // Register custom serializers
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addSerializer(ZonedDateTime.class, new CustomZonedDateTimeSerializer(zonedDateTimeFormatter, defaultTimezone));

        // Register custom deserializers
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer(zonedDateTimeFormatter, defaultTimezone));

        return Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule)
                .timeZone(TimeZone.getTimeZone(defaultTimezone))
                .simpleDateFormat(dateTimeFormat)
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        SerializationFeature.FAIL_ON_EMPTY_BEANS
                )
                .featuresToEnable(
                        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                        SerializationFeature.WRITE_DATES_WITH_ZONE_ID
                )
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .simpleDateFormat(dateTimeFormat)
                .timeZone(TimeZone.getTimeZone(defaultTimezone))
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}