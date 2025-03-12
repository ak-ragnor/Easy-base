package com.easybase.core.storage;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Service for database operations.
 * This service provides low-level access to the database, including
 * connection management and DDL operations.
 */
public interface DatabaseService {

    /**
     * Gets the current data source.
     *
     * @return the data source
     */
    DataSource getDataSource();

    /**
     * Executes a DDL statement.
     *
     * @param ddl the DDL statement to execute
     * @return true if successful, false otherwise
     */
    boolean executeDDL(String ddl);

    /**
     * Checks if a table exists.
     *
     * @param tableName the table name to check
     * @return true if the table exists, false otherwise
     */
    boolean tableExists(String tableName);

    /**
     * Creates a new connection.
     *
     * @return the connection
     */
    Connection getConnection();

    /**
     * Gets the database type.
     *
     * @return the database type (e.g., "hsqldb", "postgresql")
     */
    String getDatabaseType();

    /**
     * Gets the JDBC URL.
     *
     * @return the JDBC URL
     */
    String getJdbcUrl();
}