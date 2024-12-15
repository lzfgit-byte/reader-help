package com.ilzf.readerhelper.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reader")
public class ReaderPropertyConfig {
    @PostConstruct
    public void post() {
        System.out.println("basePath:" + basePath);
    }

    private String basePath;
}
