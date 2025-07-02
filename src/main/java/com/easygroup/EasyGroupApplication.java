package com.easygroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for EasyGroup application.
 * This application allows for intelligent creation of learner groups
 * from shared lists, taking into account various criteria.
 */
@EnableScheduling
@SpringBootApplication
public class EasyGroupApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyGroupApplication.class, args);
    }
}