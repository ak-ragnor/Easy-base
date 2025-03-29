package com.easybase.generator.config;

/**
 * Options for code generation.
 */
public class GenerationOptions {

    private final String outputDirectory;
    private final boolean generateTests;
    private final boolean overwriteCustomCode;
    private final boolean verbose;

    private GenerationOptions(Builder builder) {
        this.outputDirectory = builder.outputDirectory;
        this.generateTests = builder.generateTests;
        this.overwriteCustomCode = builder.overwriteCustomCode;
        this.verbose = builder.verbose;
    }

    // Builder pattern
    public static class Builder {
        private String outputDirectory = ".";
        private boolean generateTests = true;
        private boolean overwriteCustomCode = false;
        private boolean verbose = false;

        public Builder outputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public Builder generateTests(boolean generateTests) {
            this.generateTests = generateTests;
            return this;
        }

        public Builder overwriteCustomCode(boolean overwriteCustomCode) {
            this.overwriteCustomCode = overwriteCustomCode;
            return this;
        }

        public Builder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public GenerationOptions build() {
            return new GenerationOptions(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isGenerateTests() {
        return generateTests;
    }

    public boolean isOverwriteCustomCode() {
        return overwriteCustomCode;
    }

    public boolean isVerbose() {
        return verbose;
    }
}