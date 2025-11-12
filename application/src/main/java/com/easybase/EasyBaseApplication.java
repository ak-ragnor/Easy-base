/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase;

import com.easybase.security.starter.EasyBaseSecurityAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Akhash R
 */
@EnableAsync
@EnableScheduling
@Import(EasyBaseSecurityAutoConfiguration.class)
@Slf4j
@SpringBootApplication(scanBasePackages = "com.easybase")
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