package com.easy.base.repository;


import com.easy.base.model.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigurationRepository extends MongoRepository<Configuration,String> {
}
