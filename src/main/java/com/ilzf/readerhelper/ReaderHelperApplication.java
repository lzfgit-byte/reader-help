package com.ilzf.readerhelper;

import com.ilzf.readerhelper.config.ReaderPropertyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(ReaderPropertyConfig.class)
@ComponentScan(basePackages = "com.ilzf")
public class ReaderHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReaderHelperApplication.class, args);
    }

}
