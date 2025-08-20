package com.easybase.config;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {

	private final DSLContext dsl;

	@PostConstruct
	public void initializeDatabaseExtensions() {
		dsl.execute("CREATE EXTENSION IF NOT EXISTS \"pgcrypto\";");
	}
}
