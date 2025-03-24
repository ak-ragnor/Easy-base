package com.easybase.core.storage;

import com.easybase.core.storage.config.StorageConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hsqldb.jdbc.JDBCDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * Factory bean that creates the appropriate DataSource based on configuration.
 * Handles database initialization for HSQLDB.
 */
@Component
public class DataSourceFactory implements FactoryBean<DataSource>, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    private final StorageConfig storageConfig;
    private HikariDataSource dataSource;

    @Autowired
    public DataSourceFactory(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @Override
    public DataSource getObject() throws Exception {
        if (dataSource != null) {
            return dataSource;
        }

        if (storageConfig.isEmbedded()) {
            logger.info("Configuring embedded HSQLDB database connection");
            dataSource = _createEmbeddedDataSource();
        } else {
            logger.info("Configuring PostgreSQL database connection");
            dataSource = _createPostgresDataSource();
        }

        return dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (dataSource != null) {
            // For HSQLDB, properly shut down the database
            if (storageConfig.isEmbedded()) {
                try (Connection conn = dataSource.getConnection();
                     Statement stmt = conn.createStatement()) {
                    // SHUTDOWN is a special command in HSQLDB that performs a clean shutdown
                    stmt.execute("SHUTDOWN");
                    logger.info("HSQLDB database shut down properly");
                } catch (SQLException e) {
                    logger.error("Error shutting down HSQLDB database: {}", e.getMessage(), e);
                }
            }

            // Close the data source
            dataSource.close();
            logger.info("Database connection pool closed");

            // Wait a moment for resources to be released
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Creates a HikariCP data source for embedded HSQLDB.
     */
    private HikariDataSource _createEmbeddedDataSource() {
        String path = storageConfig.getEmbeddedPath();

        // Ensure data directory exists
        File dataDir = new File(path);
        if (!dataDir.exists()) {
            boolean dirCreated = dataDir.mkdirs();
            if (!dirCreated && !dataDir.exists()) {
                throw new RuntimeException("Failed to create database directory: " + dataDir.getAbsolutePath());
            }
        }

        // Configure HikariCP for optimal HSQLDB performance
        HikariConfig config = new HikariConfig();

        // HSQLDB JDBC URL with important options:
        // - file: specifies file-based database
        // - shutdown=true: ensures proper shutdown on JVM exit
        // - hsqldb.write_delay=false: ensures immediate write to disk for durability
        // - hsqldb.default_table_type=cached: better performance for larger datasets
        String jdbcUrl = String.format("jdbc:hsqldb:file:%s/db;shutdown=true;hsqldb.write_delay=false;hsqldb.default_table_type=cached", path);

        config.setJdbcUrl(jdbcUrl);
        config.setUsername("sa");  // default HSQLDB username
        config.setPassword("");    // default HSQLDB password
        config.setDriverClassName(JDBCDriver.class.getName());

        // Connection pool settings optimized for HSQLDB
        config.setMaximumPoolSize(10);  // Max concurrent connections
        config.setMinimumIdle(2);       // Keep min 2 connections ready
        config.setIdleTimeout(60000);   // Idle connections timeout: 1 minute
        config.setMaxLifetime(1800000); // Max connection lifetime: 30 minutes
        config.setConnectionTimeout(10000); // Connection timeout: 10 seconds

        // Keep test query simple for HSQLDB
        config.setConnectionTestQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        // Additional HikariCP performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Set pool name for easier debugging
        config.setPoolName("EasyBase-HSQLDB-Pool");

        logger.info("Configured embedded HSQLDB at: {}", jdbcUrl);

        return new HikariDataSource(config);
    }

    /**
     * Creates a HikariCP data source for PostgreSQL.
     */
    private HikariDataSource _createPostgresDataSource() {
        HikariConfig config = new HikariConfig();

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                storageConfig.getPostgresqlHost(),
                storageConfig.getPostgresqlPort(),
                storageConfig.getPostgresqlDatabase());

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(storageConfig.getPostgresqlUsername());
        config.setPassword(storageConfig.getPostgresqlPassword());
        config.setDriverClassName("org.postgresql.Driver");

        // Connection pool settings optimized for PostgreSQL
        config.setMaximumPoolSize(20);  // More connections for PostgreSQL
        config.setMinimumIdle(5);       // Keep min 5 connections ready
        config.setIdleTimeout(60000);   // Idle connections timeout: 1 minute
        config.setMaxLifetime(1800000); // Max connection lifetime: 30 minutes
        config.setConnectionTimeout(30000); // Connection timeout: 30 seconds

        // PostgreSQL specific settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        // Additional PostgreSQL specific settings
        config.addDataSourceProperty("reWriteBatchedInserts", "true"); // Better batch insert performance
        config.addDataSourceProperty("connectTimeout", "10"); // 10 second connect timeout

        // Set pool name for easier debugging
        config.setPoolName("EasyBase-PostgreSQL-Pool");

        logger.info("Configured PostgreSQL at: {}", jdbcUrl);

        return new HikariDataSource(config);
    }
}