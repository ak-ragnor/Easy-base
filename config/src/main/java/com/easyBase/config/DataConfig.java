package com.easyBase.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.easyBase.domain.repository.jpa",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@PropertySource("classpath:database/database-${spring.profiles.active:dev}.properties")
public class DataConfig {

    @Value("${jpa.hibernate.ddl-auto:none}")
    private String ddlAuto;

    @Value("${jpa.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${jpa.show-sql:false}")
    private boolean showSql;

    @Value("${jpa.properties.hibernate.format_sql:false}")
    private boolean formatSql;

    @Value("${jpa.properties.hibernate.jdbc.batch_size:50}")
    private String batchSize;

    @Value("${jpa.properties.hibernate.enable_lazy_load_no_trans:true}")
    private String lazyLoadNoTrans;

    // Development DataSource (HSQLDB)
    @Bean
    @Profile({"dev", "test"})
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .setName("easyBaseDB")
                .addScript("classpath:db/schema/hsqldb/schema.sql")
                .addScript("classpath:db/data/hsqldb/initial-data.sql")
                .build();
    }

    // Production DataSource (JNDI)
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("java:comp/env/jdbc/easyBaseDB");
        jndiObjectFactoryBean.setResourceRef(true);
        jndiObjectFactoryBean.setProxyInterface(DataSource.class);

        try {
            jndiObjectFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to lookup JNDI DataSource", e);
        }

        return (DataSource) jndiObjectFactoryBean.getObject();
    }

    // Staging DataSource (Direct PostgreSQL with HikariCP)
    @Bean
    @Profile("staging")
    public DataSource stagingDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("${db.url}");
        config.setUsername("${db.username}");
        config.setPassword("${db.password}");

        // HikariCP optimization
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);

        // PostgreSQL specific optimizations
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);

        return new HikariDataSource(config);
    }

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
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        // Basic settings
        properties.setProperty("hibernate.dialect", hibernateDialect);
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        properties.setProperty("hibernate.show_sql", String.valueOf(showSql));
        properties.setProperty("hibernate.format_sql", String.valueOf(formatSql));

        // Performance settings
        properties.setProperty("hibernate.jdbc.batch_size", batchSize);
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Connection handling
        properties.setProperty("hibernate.enable_lazy_load_no_trans", lazyLoadNoTrans);
        properties.setProperty("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");

        // Second level cache
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        properties.setProperty("hibernate.cache.ehcache.missing_cache_strategy", "create");

        // Statistics (dev only)
        properties.setProperty("hibernate.generate_statistics", String.valueOf("dev".equals(activeProfile)));

        // Soft deletes - handled via @SQLDelete and @Where annotations on entities

        return properties;
    }

    // Data initialization for non-embedded databases
    @Bean
    @Profile({"staging", "prod"})
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(false);
        populator.setSeparator(";");

        // Add initialization scripts if needed
        if ("create".equals(ddlAuto) || "create-drop".equals(ddlAuto)) {
            populator.addScript(new ClassPathResource("db/data/postgresql/initial-data.sql"));
        }

        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
}