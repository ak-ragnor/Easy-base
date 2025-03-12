<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.easybase</groupId>
    <artifactId>${entity.module}</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>EasyBase ${entity.entity}</name>
    <description>EasyBase module for ${entity.entity} entity</description>

    <dependencies>
        <!-- EasyBase dependencies -->
        <dependency>
            <groupId>com.easybase</groupId>
            <artifactId>core</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.easybase</groupId>
            <artifactId>common</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.easybase</groupId>
            <artifactId>starter</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>

        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Add dependencies for related modules -->
        <#assign relatedModules = []>
        <#list entity.fields as field>
          <#if field.isRelationship() && field.targetPackage??>
            <#assign packageParts = field.targetPackage?split(".")>
            <#assign relatedModule = "easybase-" + packageParts[2]>
            <#if !relatedModules?seq_contains(relatedModule)>
              <#assign relatedModules = relatedModules + [relatedModule]>
            </#if>
          </#if>
        </#list>

        <#if relatedModules?size gt 0>
        <!-- Related module dependencies -->
        <#list relatedModules as module>
        <dependency>
            <groupId>com.easybase</groupId>
            <artifactId>${module}</artifactId>
            <version>${r"${project.version}"}</version>
        </dependency>
        </#list>
        </#if>

        <!-- Database Migration -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>