package com.easyBase.config;

import com.easyBase.common.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ComponentScan(
        basePackages = "com.easyBase",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.easyBase.config.*"),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
        }
)
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application-${spring.profiles.active:dev}.properties"),
        @PropertySource(value = "classpath:application-${user.name}.properties", ignoreResourceNotFound = true)
})
@EnableAsync
@EnableAspectJAutoProxy
public class RootConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${app.name:EasyBase}")
    private String appName;

    private final Environment env;

    public RootConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        System.out.println("==============================================");
        System.out.println("  " + appName + " Application Starting");
        System.out.println("  Active Profile: " + activeProfile);
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("==============================================");
    }

    // Static PropertySourcesPlaceholderConfigurer needed for @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(false);
        configurer.setIgnoreResourceNotFound(false);
        return configurer;
    }

    // Application Properties Bean
    @Bean
    public AppProperties appProperties() {
        return new AppProperties();
    }

    // Message Source for i18n
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:messages/messages",
                "classpath:messages/validation-messages",
                "classpath:messages/error-messages"
        );
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    // Validator with custom message source
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    // Task Executor for async operations
    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("EasyBase-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    // Async Event Multicaster
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(taskExecutor());
        return eventMulticaster;
    }

    // Profile-specific beans
    @Configuration
    @Profile("dev")
    static class DevConfig {
        @Bean
        public DevToolsConfig devToolsConfig() {
            return new DevToolsConfig();
        }
    }

    @Configuration
    @Profile("prod")
    static class ProdConfig {
        @Bean
        public ProductionOptimizationConfig productionOptimizationConfig() {
            return new ProductionOptimizationConfig();
        }
    }
}