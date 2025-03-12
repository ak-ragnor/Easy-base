package com.easybase.generator.engine;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Processes Freemarker templates to generate code.
 * This class handles the loading and processing of templates,
 * applying model data to produce the generated code.
 */
public class TemplateProcessor {
    private Configuration freemarkerConfig;

    /**
     * Constructs a new TemplateProcessor and initializes Freemarker.
     */
    public TemplateProcessor() {
        initializeFreemarker();
    }

    /**
     * Initializes the Freemarker configuration.
     */
    private void initializeFreemarker() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);
        freemarkerConfig.setWrapUncheckedExceptions(true);
    }

    /**
     * Processes a template with the given model data.
     *
     * @param templateName The name of the template to process
     * @param model The model data to apply to the template
     * @return The processed template as a string
     * @throws IOException If there's an error loading the template
     */
    public String processTemplate(String templateName, Map<String, Object> model) throws IOException {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException("Error processing template: " + templateName, e);
        }
    }
}