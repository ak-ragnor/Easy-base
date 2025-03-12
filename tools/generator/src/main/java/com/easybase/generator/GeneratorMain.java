package com.easybase.generator;

import com.easybase.generator.engine.Generator;

import java.io.File;
import java.io.IOException;

/**
 * Main entry point for the code generator when run as a standalone application.
 * This class provides a command-line interface for the code generation tool.
 */
public class GeneratorMain {

    /**
     * Main method for running the generator from the command line.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java com.easybase.generator.GeneratorMain <yaml-file> [output-directory]");
            System.exit(1);
        }

        String yamlFile = args[0];
        File file = new File(yamlFile);

        if (!file.exists() || !file.isFile()) {
            System.err.println("YAML file not found: " + yamlFile);
            System.exit(1);
        }

        try {
            Generator generator = new Generator();

            // Set output directory if provided
            if (args.length > 1) {
                generator.setOutputDirectory(args[1]);
            }

            boolean success = generator.generate(file);

            if (!success) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}