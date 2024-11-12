package com.easy.base.repository;

import com.easy.base.entity.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigurationRepository extends MongoRepository<Configuration,String> {
}
