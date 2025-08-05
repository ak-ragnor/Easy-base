package com.easybase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class EasyBaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyBaseApplication.class, args);
        log.info("=====================================");
        log.info("EasyBase Application Started Successfully!");
        log.info("Access the application at: http://localhost:8080");
        log.info("=====================================");
    }
}
