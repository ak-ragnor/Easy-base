package com.easybase.generator.mojo;

import com.easybase.generator.engine.Generator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * Goal to generate a service from a YAML definition file.
 * Usage: mvn easybase:generate -Dservice=user
 */
@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The service name to generate.
     */
    @Parameter(property = "service", required = true)
    private String service;

    /**
     * The directory containing YAML files.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/sample")
    private File yamlDirectory;

    /**
     * The output directory for generated code.
     */
    @Parameter(defaultValue = "${project.basedir}/../../apps")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File yamlFile = new File(yamlDirectory, service + ".yml");

        getLog().info("Generating service '" + service + "' from YAML file: " + yamlFile.getAbsolutePath());

        if (!yamlFile.exists() || !yamlFile.isFile()) {
            throw new MojoExecutionException("YAML file not found: " + yamlFile.getAbsolutePath());
        }

        try {
            Generator generator = new Generator();
            generator.setOutputDirectory(outputDirectory.getAbsolutePath());

            boolean success = generator.generate(yamlFile);

            if (!success) {
                throw new MojoFailureException("Code generation failed. See log for details.");
            }

            getLog().info("Service generation completed successfully");
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating code: " + e.getMessage(), e);
        }
    }
}