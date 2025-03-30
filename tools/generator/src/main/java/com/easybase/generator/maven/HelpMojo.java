package com.easybase.generator.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Display help information on easybase-generator.
 */
@Mojo(name = "help", requiresProject = false)
public class HelpMojo extends AbstractMojo {

    /**
     * Display help information.
     */
    @Parameter(property = "detail", defaultValue = "false")
    private boolean detail;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("EasyBase Code Generator Plugin");
        getLog().info("-----------------------------");
        getLog().info("The EasyBase Code Generator Plugin is used to generate code from YAML definition files.");
        getLog().info("");
        getLog().info("Available goals:");
        getLog().info("  easybase:generate-module   - Generate code from a YAML definition file");
        getLog().info("  easybase:help              - Display this help information");

        if (detail) {
            getLog().info("");
            getLog().info("Configuration options:");
            getLog().info("  generate-module:");
            getLog().info("    -Dentity=<name>            - The entity name (required)");
            getLog().info("    -DyamlFile=<file>          - The YAML definition file (required)");
            getLog().info("    -DoutputDirectory=<dir>    - The output directory (default: ${project.basedir})");
            getLog().info("    -DgenerateTests=<boolean>  - Whether to generate test classes (default: true)");
            getLog().info("    -DoverwriteCustomCode=<boolean> - Whether to overwrite custom code (default: false)");
            getLog().info("    -Dverbose=<boolean>        - Whether to enable verbose logging (default: false)");
            getLog().info("");
            getLog().info("Example usage:");
            getLog().info("  mvn easybase:generate-module -Dentity=product -DyamlFile=../../apps/definitions/product.yml -DoutputDirectory=../../apps");
        }
    }
}