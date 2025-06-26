package com.easyBase.domain.config;

import com.easyBase.common.config.ModuleConfiguration;
import com.easyBase.common.config.ConfigProviderRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * Data Module Configuration
 * Contains all database and JPA related configuration
 * This is the final form after migrating from config module to domain module
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.easyBase.domain.repository.jpa",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableJpaAuditing
@PropertySource("classpath:database/database-${spring.profiles.active:dev}.properties")
public class DataModuleConfig implements ModuleConfiguration {

    @Autowired
    private Environment env;

    @Autowired
    private ConfigProviderRegistry registry;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${db.jndi.name:}")
    private String jndiName;

    @Value("${db.driver:org.hsqldb.jdbc.JDBCDriver}")
    private String dbDriver;

    @Value("${db.url:jdbc:hsqldb:mem:testdb}")
    private String dbUrl;

    @Value("${db.username:sa}")
    private String dbUsername;

    @Value("${db.password:}")
    private String dbPassword;

    @Value("${db.hikari.pool-size:10}")
    private int hikariPoolSize;

    @Value("${db.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Value("${jpa.hibernate.dialect:}")
    private String hibernateDialect;

    @Value("${jpa.show-sql:false}")
    private boolean showSql;

    @Value("${jpa.format-sql:false}")
    private boolean formatSql;

    @Override
    public String getModuleName() {
        return "Data";
    }

    @Override
    public int getOrder() {
        // Load early as other modules depend on data
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }

    @Override
    public boolean isEnabled() {
        return true; // Always enabled
    }

    @PostConstruct
    public void initialize() {
        System.out.println("=== Data Module Configuration ===");
        System.out.println("Module: " + getModuleName());
        System.out.println("Active Profile: " + activeProfile);
        System.out.println("Database Type: " + detectDatabaseType());
        System.out.println("JNDI Name: " + (jndiName.isEmpty() ? "Not configured" : jndiName));
        System.out.println("JPA DDL Auto: " + ddlAuto);
        System.out.println("Show SQL: " + showSql);
        System.out.println("=================================");

        // Register data configuration provider if needed
        registry.register(DataConfigProvider.class, new DataConfigProviderImpl());
    }

    @Override
    public void validate() throws ConfigurationException {
        if (!"dev".equals(activeProfile) && !"test".equals(activeProfile)) {
            if (jndiName.isEmpty() && dbUrl.contains("hsqldb")) {
                throw new ConfigurationException(
                        "HSQLDB should not be used in production. Configure JNDI or external database."
                );
            }
        }
    }

    /**
     * DataSource configuration
     * Uses JNDI in production, direct connection in dev/test
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        if (!jndiName.isEmpty() && !activeProfile.equals("dev")) {
            return jndiDataSource();
        }

        return hikariDataSource();
    }

    /**
     * JNDI DataSource for production
     */
    private DataSource jndiDataSource() {
        JndiObjectFactoryBean jndiFactory = new JndiObjectFactoryBean();
        jndiFactory.setJndiName(jndiName);
        jndiFactory.setResourceRef(true);
        jndiFactory.setProxyInterface(DataSource.class);

        try {
            jndiFactory.afterPropertiesSet();
            System.out.println("Using JNDI DataSource: " + jndiName);
            return (DataSource) jndiFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to lookup JNDI DataSource: " + jndiName, e);
        }
    }

    /**
     * HikariCP DataSource for development/test
     */
    private DataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();

        // Basic configuration
        config.setDriverClassName(dbDriver);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        // Pool configuration
        config.setMaximumPoolSize(hikariPoolSize);
        config.setMinimumIdle(Math.min(hikariPoolSize / 2, 5));
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes

        // Performance optimizations
        config.setAutoCommit(false);
        config.setConnectionTestQuery(getValidationQuery());
        config.setPoolName("EasyBase-DataSource");

        // Database-specific optimizations
        configureHikariForDatabase(config);

        return new HikariDataSource(config);
    }

    /**
     * Development-only embedded HSQLDB
     */
    @Bean
    @Profile("dev")
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:db/schema/hsqldb/schema.sql")
                .addScript("classpath:db/data/hsqldb/initial-data.sql")
                .build();
    }

    /**
     * Entity Manager Factory
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setPackagesToScan("com.easyBase.domain.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl("create".equals(ddlAuto) || "create-drop".equals(ddlAuto));
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaProperties(hibernateProperties());
        em.setPersistenceUnitName("easyBasePU");

        return em;
    }

    /**
     * Transaction Manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }

    /**
     * Hibernate properties
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();

        // Basic properties
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        properties.setProperty("hibernate.show_sql", String.valueOf(showSql));
        properties.setProperty("hibernate.format_sql", String.valueOf(formatSql));

        // Dialect
        if (!hibernateDialect.isEmpty()) {
            properties.setProperty("hibernate.dialect", hibernateDialect);
        } else {
            properties.setProperty("hibernate.dialect", detectDialect());
        }

        // Performance optimizations
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Second-level cache (if using)
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.region.factory_class",
                "org.hibernate.cache.jcache.internal.JCacheRegionFactory");

        // Statistics (dev only)
        if ("dev".equals(activeProfile)) {
            properties.setProperty("hibernate.generate_statistics", "true");
        }

        // Connection handling
        properties.setProperty("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");

        return properties;
    }

    /**
     * Detect database type from URL
     */
    private String detectDatabaseType() {
        if (dbUrl.contains("hsqldb")) return "HSQLDB";

        if (dbUrl.contains("postgresql")) return "PostgreSQL";

        return "Unknown";
    }

    /**
     * Detect Hibernate dialect
     */
    private String detectDialect() {
        String dbType = detectDatabaseType();

        return switch (dbType) {
            case "HSQLDB" -> "org.hibernate.dialect.HSQLDialect";
            case "PostgreSQL" -> "org.hibernate.dialect.PostgreSQLDialect";
            default -> "org.hibernate.dialect.HSQLDialect";
        };
    }

    /**
     * Get validation query for connection testing
     */
    private String getValidationQuery() {
        String dbType = detectDatabaseType();

        return switch (dbType) {
            case "HSQLDB" -> "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
            case "PostgreSQL" -> "SELECT 1";
            default -> "SELECT 1";
        };
    }

    /**
     * Configure HikariCP for specific database
     */
    private void configureHikariForDatabase(HikariConfig config) {
        String dbType = detectDatabaseType();

        if (dbType.equals("PostgreSQL")) {
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("useServerPrepStmts", "true");
        }
    }

    /**
     * Data configuration provider interface
     */
    public interface DataConfigProvider {
        DataSource getDataSource();
        String getDatabaseType();
        boolean isProductionMode();
    }

    /**
     * Data configuration provider implementation
     */
    private class DataConfigProviderImpl implements DataConfigProvider {
        @Override
        public DataSource getDataSource() {
            return dataSource();
        }

        @Override
        public String getDatabaseType() {
            return detectDatabaseType();
        }

        @Override
        public boolean isProductionMode() {
            return "prod".equals(activeProfile) || "production".equals(activeProfile);
        }
    }
}