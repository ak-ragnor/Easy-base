package com.easybase.core.storage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.easybase.core.storage.config.ElasticsearchConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Factory bean that creates the Elasticsearch client based on configuration.
 */
@Component
public class ElasticsearchClientFactory implements FactoryBean<ElasticsearchClient>, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchClientFactory.class);

    private final ElasticsearchConfig elasticsearchConfig;
    private final ObjectMapper objectMapper;

    private ElasticsearchClient client;
    private RestClient restClient;
    private EmbeddedElasticsearch embeddedElasticsearch;

    @Autowired
    public ElasticsearchClientFactory(ElasticsearchConfig elasticsearchConfig, ObjectMapper objectMapper) {
        this.elasticsearchConfig = elasticsearchConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public ElasticsearchClient getObject() throws Exception {
        if (client != null) {
            return client;
        }

        if (elasticsearchConfig.isEmbedded()) {
            logger.info("Configuring embedded Elasticsearch client");

            // Initialize and start embedded Elasticsearch using the Builder pattern
            embeddedElasticsearch = new EmbeddedElasticsearch.Builder(elasticsearchConfig.getVersion())
                    .port(elasticsearchConfig.getPort())
                    .dataDirectory(Paths.get("./data/elasticsearch"))
                    .downloadDirectory(Paths.get("./downloads"))
                    .progressListener(new EmbeddedElasticsearch.DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesRead, long totalBytes, int percentage) {
                            logger.info("Downloading Elasticsearch: {}% ({} / {} bytes)",
                                    percentage, bytesRead, totalBytes);
                        }

                        @Override
                        public void onComplete(long totalBytes) {
                            logger.info("Elasticsearch download completed: {} bytes", totalBytes);
                        }

                        @Override
                        public void onError(Exception e) {
                            logger.error("Elasticsearch download error: {}", e.getMessage(), e);
                        }
                    })
                    .build();

            // Start Elasticsearch and wait for it to be ready
            CompletableFuture<Void> startFuture = embeddedElasticsearch.start();

            // Wait up to 2 minutes for Elasticsearch to start
            startFuture.get(2, TimeUnit.MINUTES);

            // Create rest client to connect to embedded Elasticsearch
            restClient = RestClient.builder(
                            new HttpHost("localhost", embeddedElasticsearch.getPort(), "http"))
                    .build();
        } else {
            logger.info("Configuring external Elasticsearch client");
            // Connect to external Elasticsearch
            RestClientBuilder builder = RestClient.builder(
                    new HttpHost(elasticsearchConfig.getHost(),
                            elasticsearchConfig.getPort(), "http"));

            // Add authentication if credentials are provided
            String username = elasticsearchConfig.getUsername();
            if (username != null && !username.isEmpty()) {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(
                                username,
                                elasticsearchConfig.getPassword()));

                builder.setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }

            restClient = builder.build();
        }

        // Create the transport with Jackson mapper for JSON handling
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);

        client = new ElasticsearchClient(transport);
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return ElasticsearchClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (restClient != null) {
            try {
                restClient.close();
                logger.info("Elasticsearch REST client closed");
            } catch (Exception e) {
                logger.error("Error closing Elasticsearch REST client: {}", e.getMessage(), e);
            }
        }

        if (embeddedElasticsearch != null) {
            try {
                embeddedElasticsearch.stop();
                logger.info("Embedded Elasticsearch stopped");
            } catch (Exception e) {
                logger.error("Error stopping embedded Elasticsearch: {}", e.getMessage(), e);
            }
        }
    }
}