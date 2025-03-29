package com.easybase.generator.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Processor for FreeMarker templates.
 */
public class TemplateProcessor {

    private final Configuration configuration;

    /**
     * Constructs a new TemplateProcessor.
     */
    public TemplateProcessor() {
        this.configuration = _initializeConfiguration();
    }

    /**
     * Initializes the FreeMarker configuration.
     */
    private Configuration _initializeConfiguration() {
        Configuration config = new Configuration(Configuration.VERSION_2_3_32);
        config.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        config.setDefaultEncoding(StandardCharsets.UTF_8.name());
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        return config;
    }

    /**
     * Processes a template with the given context.
     *
     * @param templateName The template name
     * @param context The template context
     * @return The processed template as a string
     * @throws IOException If an I/O error occurs
     */
    public String process(String templateName, TemplateContext context) throws IOException {
        try (Writer writer = new StringWriter()) {
            Template template = configuration.getTemplate(templateName);
            template.process(context.getModel(), writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException("Error processing template: " + templateName, e);
        }
    }

    /**
     * Processes a template with the given context and writes the result to a writer.
     *
     * @param templateName The template name
     * @param context The template context
     * @param writer The writer to write to
     * @throws IOException If an I/O error occurs
     */
    public void process(String templateName, TemplateContext context, Writer writer) throws IOException {
        try {
            Template template = configuration.getTemplate(templateName);
            template.process(context.getModel(), writer);
        } catch (TemplateException e) {
            throw new IOException("Error processing template: " + templateName, e);
        }
    }
}