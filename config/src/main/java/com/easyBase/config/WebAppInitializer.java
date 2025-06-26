package com.easyBase.config;

import jakarta.servlet.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.io.IOException;
import java.util.*;

/**
 * Web Application Initializer - Final Version
 * Property-driven module loading with zero compile-time dependencies
 *
 * This approach allows complete module independence and configurability
 * Modules are loaded based on modules.properties configuration
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private Properties moduleProperties;

    public WebAppInitializer() {
        loadModuleProperties();
    }

    private void loadModuleProperties() {
        moduleProperties = new Properties();
        try {
            moduleProperties.load(new ClassPathResource("modules.properties").getInputStream());
        } catch (IOException e) {
            System.err.println("WARNING: modules.properties not found - using defaults");
            // Set defaults
            moduleProperties.setProperty("modules.config.core", "com.easyBase.config.RootConfig,com.easyBase.config.DataConfig");
            moduleProperties.setProperty("modules.config.security", "com.easyBase.security.config.SecurityModuleConfig");
            moduleProperties.setProperty("modules.config.web", "com.easyBase.web.config.WebModuleConfig");
            moduleProperties.setProperty("modules.security.enabled", "true");
            moduleProperties.setProperty("modules.web.enabled", "true");
        }
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        List<Class<?>> configs = new ArrayList<>();
        String activeProfile = System.getProperty("spring.profiles.active", "dev");

        System.out.println("=== Loading Module Configurations ===");
        System.out.println("Active Profile: " + activeProfile);

        // Load core modules (always loaded)
        String coreModules = moduleProperties.getProperty("modules.config.core", "");
        for (String className : coreModules.split(",")) {
            Class<?> configClass = loadClass(className.trim());
            if (configClass != null) {
                configs.add(configClass);
                System.out.println("Loaded core: " + configClass.getSimpleName());
            }
        }

        // Load feature modules based on enabled flags and profiles
        loadFeatureModule(configs, "security", activeProfile);
        loadFeatureModule(configs, "web", activeProfile);
        loadFeatureModule(configs, "monitoring", activeProfile);
        loadFeatureModule(configs, "integration", activeProfile);

        // Future modules can be added to modules.properties without code changes

        System.out.println("Total configurations loaded: " + configs.size());
        System.out.println("=====================================");

        return configs.toArray(new Class<?>[0]);
    }

    private void loadFeatureModule(List<Class<?>> configs, String moduleName, String activeProfile) {
        // Check if module is enabled
        boolean enabled = Boolean.parseBoolean(
                moduleProperties.getProperty("modules." + moduleName + ".enabled", "false")
        );

        if (!enabled) {
            System.out.println("Module " + moduleName + " is disabled");
            return;
        }

        // Check if module has profile restrictions
        String profileRestrictions = moduleProperties.getProperty("modules." + moduleName + ".profiles", "");
        if (!profileRestrictions.isEmpty()) {
            List<String> allowedProfiles = Arrays.asList(profileRestrictions.split(","));
            if (!allowedProfiles.contains(activeProfile)) {
                System.out.println("Module " + moduleName + " not loaded - profile mismatch");
                return;
            }
        }

        // Load module configuration class
        String className = moduleProperties.getProperty("modules.config." + moduleName, "");
        if (!className.isEmpty()) {
            Class<?> configClass = loadClass(className);
            if (configClass != null) {
                configs.add(configClass);
                System.out.println("Loaded " + moduleName + ": " + configClass.getSimpleName());
            }
        }
    }

    private Class<?> loadClass(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Module not found: " + className + " (module may not be in classpath)");
            return null;
        } catch (Exception e) {
            System.err.println("Error loading module: " + className + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null; // Single context approach
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/api/*", "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return new Filter[] { encodingFilter };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");

        registration.setMultipartConfig(
                new MultipartConfigElement("/tmp",
                        10 * 1024 * 1024,   // 10MB max file size
                        50 * 1024 * 1024,   // 50MB max request size
                        0)
        );
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

        System.out.println("=====================================================");
        System.out.println("  EasyBase Application Starting");
        System.out.println("  Configuration Mode: Property-Driven");
        System.out.println("  Module Dependencies: None (Dynamic Loading)");
        System.out.println("=====================================================");
    }
}