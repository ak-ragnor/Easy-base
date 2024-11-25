package com.easy.base.service.impl;

import com.easy.base.model.Configuration;

import com.easy.base.repository.ConfigurationRepository;
import com.easy.base.service.ConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class ConfiguraionServiceImpl implements ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public ConfiguraionServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void saveConfiguration(String clazz, String config) {
        configurationRepository.save(Configuration.builder().className(clazz).config(config).build());
    }

    @Override
    public Configuration getByClassName(String clazz) {
        return configurationRepository.findById(clazz).orElse(null);
    }
}
