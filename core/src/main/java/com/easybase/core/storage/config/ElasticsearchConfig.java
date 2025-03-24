package com.easybase.core.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Elasticsearch.
 */
@Configuration
public class ElasticsearchConfig {

    private final String version;
    private final boolean embedded;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public ElasticsearchConfig(
            @Value("${elasticsearch.version:8.17.0}") String version,
            @Value("${elasticsearch.embedded:true}") boolean embedded,
            @Value("${elasticsearch.host:localhost}") String host,
            @Value("${elasticsearch.port:9200}") int port,
            @Value("${elasticsearch.username:}") String username,
            @Value("${elasticsearch.password:}") String password) {

        this.version = version;
        this.embedded = embedded;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}