package com.easyBase.common.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.*;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventListener.class);

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        logger.info("Application context refreshed");
    }

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        logger.info("Application context started");
    }

    @EventListener
    public void handleContextStopped(ContextStoppedEvent event) {
        logger.info("Application context stopped");
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        logger.info("Application context closed");
    }
}