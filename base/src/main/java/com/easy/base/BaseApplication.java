package com.easy.base;

import com.easy.base.service.MediaFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BaseApplication implements CommandLineRunner {
	@Autowired
	MediaFolderService mediaFolderService;
	public static void main(String[] args) {
		SpringApplication.run(BaseApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mediaFolderService.deleteFolders( "random");
	}
}
