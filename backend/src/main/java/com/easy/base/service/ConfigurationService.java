package com.easy.base.service;

import com.easy.base.model.Configuration;

public interface ConfigurationService {
    public void saveConfiguration(String clazz,String config);
    public Configuration getByClassName(String clazz);
}
