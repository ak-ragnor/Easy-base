package com.easyBase.common.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AssignableTypeFilter;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Module Scanner with Auto-discovery
 * Automatically finds and orders module configurations
 */
@Configuration
public class ModuleConfigurationScanner {

    private final List<ModuleInfo> discoveredModules = new ArrayList<>();

    @PostConstruct
    public void scanForModules() {
        System.out.println("=== Enhanced Module Scanner Starting ===");

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AssignableTypeFilter(ModuleConfiguration.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.easyBase");

        for (BeanDefinition beanDef : candidates) {
            try {
                Class<?> clazz = Class.forName(beanDef.getBeanClassName());

                if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                if (AnnotationUtils.findAnnotation(clazz, Configuration.class) != null) {
                    ModuleConfiguration instance = (ModuleConfiguration) clazz.getDeclaredConstructor().newInstance();

                    ModuleInfo info = new ModuleInfo(
                            clazz,
                            instance.getModuleName(),
                            instance.getOrder(),
                            instance.isEnabled(),
                            detectModuleType(clazz)
                    );

                    discoveredModules.add(info);
                    System.out.println("Discovered: " + info);
                }
            } catch (Exception e) {
                System.err.println("Failed to analyze: " + beanDef.getBeanClassName());
            }
        }

        // Sort by order
        discoveredModules.sort(Comparator.comparingInt(ModuleInfo::getOrder));

        System.out.println("Total modules discovered: " + discoveredModules.size());
        System.out.println("Load order:");
        discoveredModules.forEach(m ->
                System.out.println("  " + m.getOrder() + ": " + m.getName() + " (" + m.getType() + ")")
        );
        System.out.println("========================================");
    }

    private String detectModuleType(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        if (packageName.contains(".security.")) return "Security";
        if (packageName.contains(".web.")) return "Web";
        if (packageName.contains(".domain.")) return "Data";
        if (packageName.contains(".monitoring.")) return "Monitoring";
        if (packageName.contains(".integration.")) return "Integration";
        if (packageName.contains(".config")) return "Core";
        return "Unknown";
    }

    /**
     * Get discovered module classes for loading
     */
    public List<Class<?>> getModuleClasses() {
        return discoveredModules.stream()
                .filter(ModuleInfo::isEnabled)
                .map(ModuleInfo::getModuleClass)
                .collect(Collectors.toList());
    }

    /**
     * Get discovered module classes filtered by profile
     */
    public List<Class<?>> getModuleClassesForProfile(String profile) {
        return discoveredModules.stream()
                .filter(ModuleInfo::isEnabled)
                .filter(m -> isModuleActiveForProfile(m, profile))
                .map(ModuleInfo::getModuleClass)
                .collect(Collectors.toList());
    }

    private boolean isModuleActiveForProfile(ModuleInfo module, String activeProfile) {
        // Check if module has profile restrictions
        org.springframework.context.annotation.Profile profileAnnotation =
                AnnotationUtils.findAnnotation(module.getModuleClass(), org.springframework.context.annotation.Profile.class);

        if (profileAnnotation == null) {
            return true; // No profile restriction
        }

        return Arrays.asList(profileAnnotation.value()).contains(activeProfile);
    }

    @Bean
    public ModuleScannerInfo moduleScannerInfo() {
        return new ModuleScannerInfo(discoveredModules);
    }

    /**
     * Information about a discovered module
     */
    public static class ModuleInfo {
        private final Class<?> moduleClass;
        private final String name;
        private final int order;
        private final boolean enabled;
        private final String type;

        public ModuleInfo(Class<?> moduleClass, String name, int order, boolean enabled, String type) {
            this.moduleClass = moduleClass;
            this.name = name;
            this.order = order;
            this.enabled = enabled;
            this.type = type;
        }

        public Class<?> getModuleClass() { return moduleClass; }
        public String getName() { return name; }
        public int getOrder() { return order; }
        public boolean isEnabled() { return enabled; }
        public String getType() { return type; }

        @Override
        public String toString() {
            return String.format("Module[%s, type=%s, order=%d, enabled=%s]",
                    name, type, order, enabled);
        }
    }

    /**
     * Bean containing scanner results
     */
    public static class ModuleScannerInfo {
        private final List<ModuleInfo> modules;

        public ModuleScannerInfo(List<ModuleInfo> modules) {
            this.modules = new ArrayList<>(modules);
        }

        public List<ModuleInfo> getModules() {
            return new ArrayList<>(modules);
        }

        public List<ModuleInfo> getEnabledModules() {
            return modules.stream()
                    .filter(ModuleInfo::isEnabled)
                    .collect(Collectors.toList());
        }

        public Map<String, List<ModuleInfo>> getModulesByType() {
            return modules.stream()
                    .collect(Collectors.groupingBy(ModuleInfo::getType));
        }
    }
}