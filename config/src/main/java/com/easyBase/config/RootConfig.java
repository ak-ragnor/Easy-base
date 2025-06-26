package com.easyBase.config;

import com.easyBase.common.config.ModuleConfiguration;
import com.easyBase.common.config.SharedBeansConfig;
import com.easyBase.common.properties.AppProperties;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * This is the minimal core configuration after full modularization.
 * All module-specific configurations have been moved to their respective modules.
 *
 * Responsibilities:
 * - Core application beans (async, messaging, validation)
 * - Property loading
 * - Module scanning coordination
 */
@Configuration
@Import({
        SharedBeansConfig.class,        // Shared beans and registry
        ModuleConfiguration.class     // Enhanced module discovery
})
@ComponentScan(
        basePackages = {"com.easyBase.common", "com.easyBase.service"},
        excludeFilters = {
                // Exclude module configs - they're loaded by WebAppInitializer
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.easyBase.*.config.*ModuleConfig")
        }
)
@PropertySources({
        @PropertySource("classpath:application.properties"),
})
@EnableAsync
@EnableAspectJAutoProxy
public class RootConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${app.name:EasyBase}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final Environment env;

    public RootConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        System.out.println("================================================");
        System.out.println("  " + appName + " Application Starting");
        System.out.println("  Version: " + appVersion);
        System.out.println("  Profile: " + activeProfile);
        System.out.println("  Java: " + System.getProperty("java.version"));
        System.out.println("  ");
        System.out.println("  Configuration: Fully Distributed (Phase 5)");
        System.out.println("  Module Loading: Property-Driven");
        System.out.println("  Auto-Discovery: Enhanced Scanner Active");
        System.out.println("================================================");
    }

    /**
     * Async task executor for @Async methods
     */
    @Bean
    @ConditionalOnProperty(name = "app.async.enabled", havingValue = "true", matchIfMissing = true)
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Configure from properties with defaults
        executor.setCorePoolSize(env.getProperty("app.async.core-pool-size", Integer.class, 5));
        executor.setMaxPoolSize(env.getProperty("app.async.max-pool-size", Integer.class, 20));
        executor.setQueueCapacity(env.getProperty("app.async.queue-capacity", Integer.class, 100));
        executor.setKeepAliveSeconds(env.getProperty("app.async.keep-alive-seconds", Integer.class, 60));

        // Naming and policies
        executor.setThreadNamePrefix(appName + "-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }

    /**
     * Application event multicaster with async support
     */
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(AsyncTaskExecutor taskExecutor) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(taskExecutor);
        return eventMulticaster;
    }

    /**
     * Message source for internationalization
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(
                "classpath:messages/messages",
                "classpath:messages/validation-messages",
                "classpath:messages/error-messages",
                "classpath:messages/business-messages"
        );

        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(env.getProperty("app.messages.cache-seconds", Integer.class, 3600));
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    /**
     * Validator factory bean with message source
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    /**
     * Application properties bean
     */
    @Bean
    public AppProperties appProperties() {
        return new AppProperties();
    }

    /**
     * Application metadata
     */
    @Bean
    public ApplicationMetadata applicationMetadata() {
        return new ApplicationMetadata(appName, appVersion, activeProfile, "Phase 5 - Final");
    }

    /**
     * Application metadata holder
     */
    public static class ApplicationMetadata {
        private final String name;
        private final String version;
        private final String profile;
        private final String configPhase;

        public ApplicationMetadata(String name, String version, String profile, String configPhase) {
            this.name = name;
            this.version = version;
            this.profile = profile;
            this.configPhase = configPhase;
        }

        public String getName() { return name; }
        public String getVersion() { return version; }
        public String getProfile() { return profile; }
        public String getConfigPhase() { return configPhase; }
    }
}