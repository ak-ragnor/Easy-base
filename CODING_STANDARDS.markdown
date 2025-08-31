# EasyBase Coding Standards

This document outlines the coding standards implemented for the EasyBase project using Liferay Source Formatter for comprehensive code formatting and standardization.

## Implementation

**Framework**: Liferay Source Formatter with custom configuration

**Configuration**: Located in `source-formatter.properties` and Maven exec plugin in `pom.xml`

## Automated Standards ✅ WORKING

The following standards are automatically enforced by Liferay Source Formatter:

### 1. Import Organization
- Imports are automatically organized in the order: `com`, `jakarta`, `java`, `javax`, `org`
- Unused imports are automatically removed
- Import statements are properly formatted

### 2. Code Formatting
- Consistent indentation using tabs
- Proper line breaks and spacing
- Method call formatting with appropriate line breaks
- Annotation ordering and placement

### 3. Class Member Organization
- Constants moved to the bottom of the class (following Liferay conventions)
- Methods and fields properly ordered
- Constructor parameters handled appropriately

### 4. XML/POM Formatting
- XML files formatted with consistent tab indentation
- Proper element ordering and spacing
- Clean XML structure throughout the project

## Manual Coding Standards

The following standards should be followed manually during development:

### 1. Builders
- Use `*Builder` when possible for creating complex objects
- Format builder calls across multiple lines for readability

**Example**:
```java
// Good
SystemInfo newInfo = SystemInfo.builder()
    .appVersion(_APP_VERSION)
    .dbVersion(_DB_VERSION)
    .status("ACTIVE")
    .build();
```

### 2. Method Chaining
- Avoid excessive method chaining except for allowed types
- **Allowed chaining types**: `*Builder`, `Optional`, `Stream`, `JSONObject`
- Break long chains across multiple lines

### 3. Constructor Parameters
- Variables assigned from constructor parameters should come first
- Follow the order as they appear in the constructor signature
- Other variables should come last

### 4. Enhanced For-Loop
- Use enhanced for-loops when possible instead of traditional for-loops
- Applies to both arrays and collections

### 5. Collections Optimization
- Use `Collections.addAll(collection, items...)` instead of `collection.addAll(Arrays.asList(items...))`
- Use appropriate collection types for performance

### 6. Lambda Expression Simplification
- Use expression lambdas instead of single-statement blocks
- Example: `item -> item.startsWith("a")` instead of `item -> { return item.startsWith("a"); }`

## Running Formatting

### Option 1: Using Maven Profile (Recommended)
```bash
mvn -PformatSource validate
```

### Option 2: Using the Shell Script
```bash
./formatSource.sh
```

### Option 3: Direct Maven Command
```bash
mvn exec:java -Dexec.mainClass=com.liferay.source.formatter.SourceFormatter -Dexec.args="-Dsource.properties.file=${PWD}/source-formatter.properties"
```

### Automatic During Build
The formatter runs automatically during the Maven build process in the `process-sources` phase:
```bash
mvn compile
```

## Configuration Files

- **Main Configuration**: `source-formatter.properties`
- **Maven Plugin**: Configured in `pom.xml` using exec-maven-plugin
- **Liferay Repository**: Configured for accessing Liferay Source Formatter artifacts

## IDE Configuration

Configure your IDE to:
- Follow the formatting standards applied by Liferay Source Formatter
- Use appropriate indentation (tabs for XML, spaces for Java)
- Run the formatter before commits for consistency