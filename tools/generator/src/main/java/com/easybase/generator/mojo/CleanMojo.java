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
 * Goal to clean a specific service.
 * Usage: mvn easybase:clean -Dservice=user
 */
@Mojo(name = "clean")
public class CleanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The service name to clean.
     */
    @Parameter(property = "service", required = true)
    private String service;

    /**
     * The directory containing app modules.
     */
    @Parameter(defaultValue = "${project.basedir}/../../apps")
    private File appsDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File serviceDir = new File(appsDirectory, "easybase-" + service);

        getLog().info("Cleaning service: " + service);

        if (!serviceDir.exists() || !serviceDir.isDirectory()) {
            throw new MojoExecutionException("Service directory not found: " + serviceDir.getAbsolutePath());
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "clean");
            processBuilder.directory(serviceDir);
            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new MojoFailureException("Clean failed with exit code: " + exitCode);
            }

            getLog().info("Service cleaned successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Error cleaning service: " + e.getMessage(), e);
        }
    }
}