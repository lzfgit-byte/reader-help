package com.ilzf.readerhelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ilzf")
public class ReaderHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReaderHelperApplication.class, args);
    }

}
