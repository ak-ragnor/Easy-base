package com.easybase.generator.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * Goal to build all services.
 * Usage: mvn easybase:all
 */
@Mojo(name = "all")
public class BuildAllMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The root project directory.
     */
    @Parameter(defaultValue = "${project.basedir}/../..")
    private File rootDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Building all services");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "clean", "install");
            processBuilder.directory(rootDirectory);
            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new MojoFailureException("Build failed with exit code: " + exitCode);
            }

            getLog().info("All services built successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Error building services: " + e.getMessage(), e);
        }
    }
}