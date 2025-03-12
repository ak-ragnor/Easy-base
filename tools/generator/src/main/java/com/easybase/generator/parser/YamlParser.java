package com.easybase.generator.parser;

import com.easybase.generator.model.EntityDefinition;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Parses YAML files into EntityDefinition objects.
 * This class handles the initial reading of YAML files and
 * delegates to YamlConverter for proper object conversion.
 */
public class YamlParser {
    private YamlConverter converter;

    /**
     * Constructs a new YamlParser with a YamlConverter.
     */
    public YamlParser() {
        this.converter = new YamlConverter();
    }

    /**
     * Parses a YAML file into an EntityDefinition.
     *
     * @param file The YAML file to parse
     * @return The parsed EntityDefinition
     * @throws IOException If there's an error reading the file
     */
    public EntityDefinition parse(File file) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            return parse(input);
        }
    }

    /**
     * Parses a YAML file path into an EntityDefinition.
     *
     * @param filePath The path to the YAML file
     * @return The parsed EntityDefinition
     * @throws IOException If there's an error reading the file
     */
    public EntityDefinition parse(String filePath) throws IOException {
        return parse(new File(filePath));
    }

    /**
     * Parses YAML content from an InputStream into an EntityDefinition.
     *
     * @param inputStream The InputStream containing YAML content
     * @return The parsed EntityDefinition
     */
    public EntityDefinition parse(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> rawYaml = yaml.load(inputStream);
        return converter.convert(rawYaml);
    }
}