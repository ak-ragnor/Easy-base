package com.easybase.core.storage;

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

/**
 * Implementation of DatabaseService.
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private String databaseType;
    private String jdbcUrl;

    @Autowired
    public DatabaseServiceImpl(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        detectDatabaseType();
    }

    /**
     * Detects the database type from the connection.
     */
    private void detectDatabaseType() {
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

            jdbcUrl = metaData.getURL();

            logger.info("Connected to database: {} ({})", databaseType, jdbcUrl);
        } catch (SQLException e) {
            logger.error("Failed to detect database type: {}", e.getMessage(), e);
            databaseType = "unknown";
            jdbcUrl = "unknown";
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean executeDDL(String ddl) {
        try {
            jdbcTemplate.execute(ddl);
            return true;
        } catch (DataAccessException e) {
            logger.error("Failed to execute DDL: {} - {}", ddl, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean tableExists(String tableName) {
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

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Failed to get connection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    @Override
    public String getDatabaseType() {
        return databaseType;
    }

    @Override
    public String getJdbcUrl() {
        return jdbcUrl;
    }
}