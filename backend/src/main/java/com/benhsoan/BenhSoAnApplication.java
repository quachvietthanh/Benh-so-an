package com.benhsoan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BenhSoAnApplication {

    public static void main(String[] args) {
        SpringApplication.run(BenhSoAnApplication.class, args);
    }
}
