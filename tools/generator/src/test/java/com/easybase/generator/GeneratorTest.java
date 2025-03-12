package com.easybase.generator;

import com.easybase.generator.engine.Generator;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testUserYamlParsing() throws IOException {
        // Get the test YAML file as a stream
        InputStream yamlStream = getClass().getClassLoader().getResourceAsStream("samples/user.yml");
        if (yamlStream == null) {
            fail("Test YAML file not found. Make sure samples/user.yml exists in test resources.");
        }

        // Parse the YAML file directly from stream
        YamlParser parser = new YamlParser();
        EntityDefinition entity = parser.parse(yamlStream);

        // Verify basic properties
        assertNotNull(entity, "Entity should not be null");
        assertEquals("User", entity.getEntity());
        assertEquals("eb_user", entity.getTable());
        assertEquals("com.easybase.user", entity.getPackageName());

        // Verify fields
        assertFalse(entity.getFields().isEmpty(), "Entity should have fields");
        assertTrue(entity.getFields().stream().anyMatch(f -> f.getName().equals("id")),
                "Entity should have id field");
        assertTrue(entity.getFields().stream().anyMatch(f -> f.getName().equals("email")),
                "Entity should have email field");

        // Verify finders
        assertFalse(entity.getFinders().isEmpty(), "Entity should have finders");
        assertTrue(entity.getFinders().stream().anyMatch(f -> f.getName().equals("findByEmailAndStatus")),
                "Entity should have findByEmailAndStatus finder");
    }

    @Test
    void testGenerateUser() throws IOException {
        // Get the test YAML file as a stream
        InputStream yamlStream = getClass().getClassLoader().getResourceAsStream("samples/user.yml");
        if (yamlStream == null) {
            fail("Test YAML file not found. Make sure samples/user.yml exists in test resources.");
        }

        // Create a temporary file for the YAML content
        Path tempYamlFile = tempDir.resolve("user.yml");
        Files.copy(yamlStream, tempYamlFile, StandardCopyOption.REPLACE_EXISTING);

        // Generate code to the temp directory
        Generator generator = new Generator();
        generator.setOutputDirectory(tempDir.toString());

        boolean success = generator.generate(tempYamlFile.toFile());

        // Verify generation was successful
        assertTrue(success, "Code generation should succeed");

        // Verify output files
        File moduleDir = tempDir.resolve("easybase-user").toFile();
        assertTrue(moduleDir.exists(), "Module directory should exist");
        assertTrue(new File(moduleDir, "pom.xml").exists(), "POM file should exist");

        // Verify Java files
        File srcDir = moduleDir.toPath()
                .resolve("src/main/java/com/easybase/user").toFile();
        assertTrue(srcDir.exists(), "Source directory should exist");

        File modelDir = new File(srcDir, "model");
        assertTrue(modelDir.exists(), "Model directory should exist");
        assertTrue(new File(modelDir, "User.java").exists(), "User interface should exist");
        assertTrue(new File(modelDir, "UserImpl.java").exists(), "UserImpl class should exist");

        // Verify application class
        assertTrue(new File(srcDir, "UserApplication.java").exists(),
                "Application class should exist");

        // Verify resources
        File resourcesDir = moduleDir.toPath().resolve("src/main/resources").toFile();
        assertTrue(resourcesDir.exists(), "Resources directory should exist");
        assertTrue(new File(resourcesDir, "application.yml").exists(),
                "Application YAML file should exist");

        File migrationsDir = new File(resourcesDir, "db/migration");
        assertTrue(migrationsDir.exists(), "Migrations directory should exist");
        assertTrue(new File(migrationsDir, "V1__create_eb_user.sql").exists(),
                "Migration SQL file should exist");
    }
}