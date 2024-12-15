package com.ilzf.readerhelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "reader")
public class ReaderPropertyConfig {

    private String path;
    private String metInfo;
}
