package com.easyBase.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name = "EasyBase";
    private String version = "1.0.0";
    private String description = "Enterprise Application Platform";

    private Security security = new Security();
    private Api api = new Api();
    private Async async = new Async();

    @Data
    public static class Security {
        private Jwt jwt = new Jwt();
        private Cors cors = new Cors();

        @Data
        public static class Jwt {
            private String secret;
            private Long expiration = 86400L; // 24 hours
            private String header = "Authorization";
            private String prefix = "Bearer ";
        }

        @Data
        public static class Cors {
            private String allowedOrigins = "*";
            private Boolean allowCredentials = true;
        }
    }

    @Data
    public static class Api {
        private String version = "v1";
        private String basePath = "/api";
        private Pagination pagination = new Pagination();

        @Data
        public static class Pagination {
            private Integer defaultPageSize = 20;
            private Integer maxPageSize = 100;
        }
    }

    @Data
    public static class Async {
        private Integer corePoolSize = 5;
        private Integer maxPoolSize = 20;
        private Integer queueCapacity = 100;
    }
}