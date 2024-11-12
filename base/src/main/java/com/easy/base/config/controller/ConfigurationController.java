package com.easy.base.config.controller;

import com.easy.base.config.Configuration;
import com.easy.base.media.config.MediaConfig;
import com.easy.base.service.ConfigurationService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/media")
    public MediaConfig getMediaConfig() throws JSONException, NoSuchFieldException, IllegalAccessException {
        Configuration<MediaConfig> mediaConfig = Configuration.of(new MediaConfig());
        return mediaConfig.fetch(configurationService);
    }
    @PostMapping("/media")
    public MediaConfig saveMediaConfig(@RequestBody MediaConfig mediaConfig) throws JSONException, IllegalAccessException {
        Configuration<MediaConfig> config = Configuration.of(mediaConfig);
        config.save(configurationService);
        return mediaConfig;
    }
}
