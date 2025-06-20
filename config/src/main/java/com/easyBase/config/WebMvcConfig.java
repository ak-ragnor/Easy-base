package com.easyBase.config;

import com.easyBase.web.converter.CustomJsonConverter;
import com.easyBase.web.interceptor.LoggingInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.easyBase.web.controller", "com.easyBase.web.advice"})
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.api.version:v1}")
    private String apiVersion;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .dateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                .serializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS); // Include nulls

        // Pretty print in dev mode
        if ("dev".equals(activeProfile)) {
            builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
        }

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));

        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .dateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                .serializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**");
    }

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }

    // Bean validation
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }

    // Configure path matching for nested resources
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(false);
    }

    // Disable default suffix pattern matching
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(true)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }
}