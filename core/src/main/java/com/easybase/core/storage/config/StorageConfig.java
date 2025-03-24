package com.easybase.core.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for database storage.
 */
@Configuration
public class StorageConfig {

    private final String type;
    private final String embeddedPath;
    private final String postgresqlHost;
    private final int postgresqlPort;
    private final String postgresqlDatabase;
    private final String postgresqlUsername;
    private final String postgresqlPassword;

    public StorageConfig(
            @Value("${storage.type:embedded}") String type,
            @Value("${storage.embedded.path:./data/hsqldb}") String embeddedPath,
            @Value("${storage.postgresql.host:localhost}") String postgresqlHost,
            @Value("${storage.postgresql.port:5432}") int postgresqlPort,
            @Value("${storage.postgresql.database:easybase}") String postgresqlDatabase,
            @Value("${storage.postgresql.username:}") String postgresqlUsername,
            @Value("${storage.postgresql.password:}") String postgresqlPassword) {

        this.type = type;
        this.embeddedPath = embeddedPath;
        this.postgresqlHost = postgresqlHost;
        this.postgresqlPort = postgresqlPort;
        this.postgresqlDatabase = postgresqlDatabase;
        this.postgresqlUsername = postgresqlUsername;
        this.postgresqlPassword = postgresqlPassword;
    }

    public String getType() {
        return type;
    }

    public String getEmbeddedPath() {
        return embeddedPath;
    }

    public String getPostgresqlHost() {
        return postgresqlHost;
    }

    public int getPostgresqlPort() {
        return postgresqlPort;
    }

    public String getPostgresqlDatabase() {
        return postgresqlDatabase;
    }

    public String getPostgresqlUsername() {
        return postgresqlUsername;
    }

    public String getPostgresqlPassword() {
        return postgresqlPassword;
    }

    public boolean isEmbedded() {
        return !"postgresql".equalsIgnoreCase(type);
    }
}