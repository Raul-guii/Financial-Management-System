package com.raul.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SgfApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgfApplication.class, args);
    }

}
