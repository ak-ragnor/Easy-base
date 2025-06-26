package com.easyBase.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for configuration providers
 * Allows modules to register and access configuration providers without direct dependencies
 * Created as a bean in SharedBeansConfig
 */
public class ConfigProviderRegistry {

    private final Map<Class<?>, Object> providers = new ConcurrentHashMap<>();

    /**
     * Register a configuration provider
     */
    public <T> void register(Class<T> providerClass, T provider) {
        providers.put(providerClass, provider);
        System.out.println("Registered provider: " + providerClass.getSimpleName());
    }

    /**
     * Get a configuration provider
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> providerClass) {
        T provider = (T) providers.get(providerClass);
        if (provider == null) {
            throw new IllegalStateException(
                    "No provider registered for: " + providerClass.getName() +
                            ". Available providers: " + providers.keySet()
            );
        }
        return provider;
    }

    /**
     * Check if a provider is registered
     */
    public boolean hasProvider(Class<?> providerClass) {
        return providers.containsKey(providerClass);
    }

    /**
     * Get optional provider
     */
    @SuppressWarnings("unchecked")
    public <T> T getOptional(Class<T> providerClass) {
        return (T) providers.get(providerClass);
    }
}