package com.easybase.generator.template;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for template processing.
 */
public class TemplateContext {

    private final Map<String, Object> model = new HashMap<>();

    /**
     * Sets a value in the model.
     *
     * @param key The key
     * @param value The value
     * @return This context
     */
    public TemplateContext set(String key, Object value) {
        model.put(key, value);
        return this;
    }

    /**
     * Sets multiple values in the model.
     *
     * @param values The values
     * @return This context
     */
    public TemplateContext setAll(Map<String, Object> values) {
        model.putAll(values);
        return this;
    }

    /**
     * Gets a value from the model.
     *
     * @param key The key
     * @return The value
     */
    public Object get(String key) {
        return model.get(key);
    }

    /**
     * Gets the model.
     *
     * @return The model
     */
    public Map<String, Object> getModel() {
        return model;
    }

    /**
     * Creates a new context.
     *
     * @return A new context
     */
    public static TemplateContext create() {
        return new TemplateContext();
    }
}