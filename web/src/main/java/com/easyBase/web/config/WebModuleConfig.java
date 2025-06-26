package com.easyBase.web.config;

import com.easyBase.common.config.ModuleConfiguration;
import com.easyBase.common.config.WebConfigProvider;
import com.easyBase.common.config.ConfigProviderRegistry;
import com.easyBase.web.interceptor.LoggingInterceptor;
import com.easyBase.web.interceptor.ServiceContextInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

/**
 * Web Module Configuration
 * Contains all Spring MVC related configuration
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.easyBase.web.controller", "com.easyBase.web.advice"})
public class WebModuleConfig implements WebMvcConfigurer, ModuleConfiguration, WebConfigProvider {

    @Value("${app.api.version:v1}")
    private String apiVersion;

    @Value("${app.api.base-path:/api}")
    private String apiBasePath;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${app.logging.requests:true}")
    private boolean requestLoggingEnabled;

    @Value("${app.api.cors.enabled:true}")
    private boolean corsEnabled;

    @Value("${app.api.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Autowired
    private ConfigProviderRegistry registry;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private ObjectMapper sharedObjectMapper; // From SharedBeansConfig

    @Override
    public String getModuleName() {
        return "Web";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20; // After Security
    }

    @PostConstruct
    public void initialize() {
        System.out.println("=== Web Module Configuration ===");
        System.out.println("Module: " + getModuleName());
        System.out.println("API Base Path: " + apiBasePath);
        System.out.println("API Version: " + apiVersion);
        System.out.println("CORS Enabled: " + corsEnabled);
        System.out.println("Request Logging: " + requestLoggingEnabled);
        System.out.println("================================");

        // Register as WebConfigProvider
        registry.register(WebConfigProvider.class, this);
    }

    @Override
    public void validate() throws ConfigurationException {
        if (apiBasePath == null || apiBasePath.isEmpty()) {
            throw new ConfigurationException("API base path cannot be empty");
        }
        if (!apiBasePath.startsWith("/")) {
            throw new ConfigurationException("API base path must start with /");
        }
    }

    // ========== WebMvcConfigurer Implementation ==========

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(sharedObjectMapper);
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON_UTF8,
                new MediaType("application", "*+json")
        ));
        return converter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Logging interceptor
        if (requestLoggingEnabled) {
            registry.addInterceptor(loggingInterceptor())
                    .addPathPatterns(apiBasePath + "/**")
                    .order(1);
        }

        // Service context interceptor
        registry.addInterceptor(serviceContextInterceptor())
                .addPathPatterns(apiBasePath + "/**")
                .order(2);
    }

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }

    @Bean
    public ServiceContextInterceptor serviceContextInterceptor() {
        return new ServiceContextInterceptor();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsEnabled) {
            registry.addMapping(apiBasePath + "/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(false);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/", "/static/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(3600);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // ========== Additional Beans ==========

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        return processor;
    }

    // ========== WebConfigProvider Implementation ==========

    @Override
    public ObjectMapper objectMapper() {
        return sharedObjectMapper;
    }

    @Override
    public void configureInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void configureCors(CorsRegistry registry) {
        addCorsMappings(registry);
    }

    @Override
    public Validator validator() {
        return validator;
    }

    @Override
    public String getApiBasePath() {
        return apiBasePath;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public boolean isRequestLoggingEnabled() {
        return requestLoggingEnabled;
    }
}