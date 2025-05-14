package com.example.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HmsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsBeApplication.class, args);
    }

}
