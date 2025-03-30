package com.easybase.generator.maven;

import com.easybase.generator.CodeGenerator;
import com.easybase.generator.config.GenerationOptions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Files;

/**
 * Maven plugin for generating code from YAML definition files.
 * This mojo implements the "generate-module" goal.
 */
@Mojo(name = "generate-module", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateModuleMojo extends AbstractMojo {

    /**
     * The entity name (used to identify the module).
     */
    @Parameter(property = "entity", required = true)
    private String entity;

    /**
     * The path to the YAML definition file.
     */
    @Parameter(property = "yamlFile", required = true)
    private File yamlFile;

    /**
     * The output directory for generated code.
     * Default is the project's base directory.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.basedir}")
    private File outputDirectory;

    /**
     * Whether to generate test classes.
     */
    @Parameter(property = "generateTests", defaultValue = "true")
    private boolean generateTests;

    /**
     * Whether to overwrite custom code.
     */
    @Parameter(property = "overwriteCustomCode", defaultValue = "false")
    private boolean overwriteCustomCode;

    /**
     * Whether to enable verbose logging.
     */
    @Parameter(property = "verbose", defaultValue = "true")
    private boolean verbose;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Log parameters for debugging
        getLog().info("Entity: " + entity);
        getLog().info("YAML file: " + (yamlFile != null ? yamlFile.getAbsolutePath() : "null"));
        getLog().info("Output directory: " + (outputDirectory != null ? outputDirectory.getAbsolutePath() : "null"));

        if (yamlFile == null || !yamlFile.exists()) {
            throw new MojoExecutionException("YAML file not found: " +
                    (yamlFile != null ? yamlFile.getAbsolutePath() : "null"));
        }

        getLog().info("Checking YAML file contents...");
        try {
            String yamlContent = Files.readString(yamlFile.toPath());
            getLog().info("YAML file exists and has content length: " + yamlContent.length());
            getLog().info("First 100 characters of YAML file: " + yamlContent.substring(0, Math.min(100, yamlContent.length())));
        } catch (Exception e) {
            getLog().error("Error reading YAML file", e);
        }

        getLog().info("Checking output directory...");
        if (!outputDirectory.exists()) {
            getLog().info("Output directory does not exist, creating it: " + outputDirectory.getAbsolutePath());
            outputDirectory.mkdirs();
        } else {
            getLog().info("Output directory exists: " + outputDirectory.getAbsolutePath());
        }

        getLog().info("Generating code for entity: " + entity);
        getLog().info("Using YAML file: " + yamlFile.getAbsolutePath());
        getLog().info("Output directory: " + outputDirectory.getAbsolutePath());

        try {
            // Create the generation options with verbose mode enabled
            GenerationOptions options = GenerationOptions.builder()
                    .outputDirectory(outputDirectory.getAbsolutePath())
                    .generateTests(generateTests)
                    .overwriteCustomCode(overwriteCustomCode)
                    .verbose(true) // Force verbose mode for debugging
                    .build();

            // Create and run the code generator
            getLog().info("Creating CodeGenerator with options: " + options);
            CodeGenerator generator = new CodeGenerator(options);

            getLog().info("Calling generate method with YAML file: " + yamlFile.getAbsolutePath());
            generator.generate(yamlFile);

            getLog().info("Code generation completed successfully for entity: " + entity);

            // List the generated files
            getLog().info("Checking for generated files in output directory...");
            File entityDir = new File(outputDirectory, "easybase-" + entity.toLowerCase());
            if (entityDir.exists()) {
                getLog().info("Found entity directory: " + entityDir.getAbsolutePath());
                _listFiles(entityDir, getLog(), "");
            } else {
                getLog().warn("Entity directory not found: " + entityDir.getAbsolutePath());
                getLog().info("Listing all directories in output directory:");
                File[] dirs = outputDirectory.listFiles(File::isDirectory);
                if (dirs != null) {
                    for (File dir : dirs) {
                        getLog().info(" - " + dir.getName());
                    }
                } else {
                    getLog().warn("No directories found in output directory");
                }
            }

        } catch (Exception e) {
            getLog().error("Error generating code for entity: " + entity, e);
            throw new MojoExecutionException("Error generating code for entity: " + entity, e);
        }
    }

    /**
     * Helper method to recursively list files in a directory.
     */
    private void _listFiles(File dir, org.apache.maven.plugin.logging.Log log, String indent) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                log.info(indent + " - " + file.getName() + (file.isDirectory() ? "/" : ""));
                if (file.isDirectory()) {
                    _listFiles(file, log, indent + "   ");
                }
            }
        }
    }
}