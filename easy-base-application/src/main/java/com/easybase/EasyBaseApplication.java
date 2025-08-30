package com.easybase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class EasyBaseApplication {
	public static void main(String[] args) {
		System.setProperty("org.jooq.no-logo", "true");
		SpringApplication.run(EasyBaseApplication.class, args);
		log.info("=====================================");
		log.info("EasyBase Application Started Successfully!");
		log.info("Access the application at: http://localhost:8080");
		log.info("=====================================");
	}
}
