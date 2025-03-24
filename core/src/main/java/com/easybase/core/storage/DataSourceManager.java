package com.easybase.core.storage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.easybase.core.storage.config.ElasticsearchConfig;
import com.easybase.core.storage.config.StorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central service for database and Elasticsearch operations.
 * Uses caching and connection pooling for optimal performance.
 */
@Service
public class DataSourceManager {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceManager.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final ElasticsearchClient elasticsearchClient;
    private final StorageConfig storageConfig;
    private final ElasticsearchConfig elasticsearchConfig;

    private String databaseType;
    private String databaseUrl;
    private String databaseVersion;

    private final Map<String, Boolean> tableExistenceCache = new ConcurrentHashMap<>();

    private final Map<String, String> ddlTemplateCache = new ConcurrentHashMap<>();

    @Autowired
    public DataSourceManager(
            DataSource dataSource,
            ElasticsearchClient elasticsearchClient,
            StorageConfig storageConfig,
            ElasticsearchConfig elasticsearchConfig) {

        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.elasticsearchClient = elasticsearchClient;
        this.storageConfig = storageConfig;
        this.elasticsearchConfig = elasticsearchConfig;

        // Initialize database metadata
        _detectDatabaseType();

        // Pre-populate DDL templates based on database type
        _initDdlTemplates();
    }

    /**
     * Gets the database DataSource.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Gets the JDBC template for SQL operations.
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * Gets the Elasticsearch client.
     */
    public ElasticsearchClient getElasticsearchClient() {
        return elasticsearchClient;
    }

    /**
     * Checks if an embedded database is being used.
     */
    public boolean isUsingEmbeddedDatabase() {
        return storageConfig.isEmbedded();
    }

    /**
     * Checks if an embedded Elasticsearch is being used.
     */
    public boolean isUsingEmbeddedElasticsearch() {
        return elasticsearchConfig.isEmbedded();
    }

    /**
     * Gets the database type (e.g., "hsqldb", "postgresql").
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * Gets the database URL.
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Gets the database version.
     */
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    /**
     * Executes a DDL statement optimized for the current database.
     *
     * @param ddl the DDL statement to execute
     * @return true if successful, false otherwise
     */
    public boolean executeDDL(String ddl) {
        try {
            // Log DDL statements at debug level
            logger.debug("Executing DDL: {}", ddl);

            // Use JdbcTemplate for safe execution
            jdbcTemplate.execute(ddl);
            return true;
        } catch (DataAccessException e) {
            logger.error("Failed to execute DDL: {} - {}", ddl, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if a table exists with caching.
     *
     * @param tableName the table name to check
     * @return true if the table exists, false otherwise
     */
    public boolean tableExists(String tableName) {
        // Check cache first
        return tableExistenceCache.computeIfAbsent(tableName.toLowerCase(), this::_checkTableExists);
    }

    /**
     * Invalidates the table existence cache for a specific table.
     * Call this method after creating or dropping tables.
     *
     * @param tableName the table name to invalidate in the cache
     */
    public void invalidateTableCache(String tableName) {
        tableExistenceCache.remove(tableName.toLowerCase());
    }

    /**
     * Creates a new connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    /**
     * Detects the database type from the connection.
     */
    private void _detectDatabaseType() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName().toLowerCase();

            if (productName.contains("postgresql")) {
                databaseType = "postgresql";
            } else if (productName.contains("hsql")) {
                databaseType = "hsqldb";
            } else {
                databaseType = productName;
            }

            databaseUrl = metaData.getURL();
            databaseVersion = metaData.getDatabaseProductVersion();

            logger.info("Connected to database: {} {} ({})",
                    databaseType, databaseVersion, databaseUrl);
        } catch (SQLException e) {
            logger.error("Failed to detect database type: {}", e.getMessage(), e);
            databaseType = "unknown";
            databaseUrl = "unknown";
            databaseVersion = "unknown";
        }
    }

    /**
     * Initialize database-specific DDL templates.
     */
    private void _initDdlTemplates() {
        if ("hsqldb".equals(databaseType)) {
            ddlTemplateCache.put("uuid_column", "VARCHAR(36)");
            ddlTemplateCache.put("timestamp_column", "TIMESTAMP");
            ddlTemplateCache.put("text_column", "VARCHAR(4000)");
            ddlTemplateCache.put("auto_increment", "BIGINT GENERATED BY DEFAULT AS IDENTITY");
        } else if ("postgresql".equals(databaseType)) {
            ddlTemplateCache.put("uuid_column", "UUID");
            ddlTemplateCache.put("timestamp_column", "TIMESTAMP WITH TIME ZONE");
            ddlTemplateCache.put("text_column", "TEXT");
            ddlTemplateCache.put("auto_increment", "BIGSERIAL");
        } else {
            // Default/fallback templates
            ddlTemplateCache.put("uuid_column", "VARCHAR(36)");
            ddlTemplateCache.put("timestamp_column", "TIMESTAMP");
            ddlTemplateCache.put("text_column", "VARCHAR(4000)");
            ddlTemplateCache.put("auto_increment", "BIGINT AUTO_INCREMENT");
        }
    }

    /**
     * Gets a DDL template for the current database type.
     *
     * @param templateName The template name
     * @return The template string or null if not found
     */
    public String getDdlTemplate(String templateName) {
        return ddlTemplateCache.get(templateName);
    }

    /**
     * Actual implementation of table existence check.
     */
    private boolean _checkTableExists(String tableName) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // Table names might be case-sensitive depending on the database
            String catalog = conn.getCatalog();
            String schema = conn.getSchema();

            // Try with exact case
            try (ResultSet tables = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    return true;
                }
            }

            // Try with uppercase (for databases that store names in uppercase)
            try (ResultSet tables = metaData.getTables(catalog, schema, tableName.toUpperCase(), new String[]{"TABLE"})) {
                if (tables.next()) {
                    return true;
                }
            }

            // Try with lowercase (for databases that store names in lowercase)
            try (ResultSet tables = metaData.getTables(catalog, schema, tableName.toLowerCase(), new String[]{"TABLE"})) {
                if (tables.next()) {
                    return true;
                }
            }

            return false;
        } catch (SQLException e) {
            logger.error("Error checking if table exists: {}", e.getMessage(), e);
            return false;
        }
    }
}