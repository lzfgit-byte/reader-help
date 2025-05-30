package com.ilzf.readerhelper.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ReaderPropertyConfig.class})
public class ReaderConfig {
}
