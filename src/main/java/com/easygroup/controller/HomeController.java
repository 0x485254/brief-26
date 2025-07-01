package com.easygroup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the root endpoint.
 * Provides a simple health check endpoint to verify the backend is running.
 */
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Root endpoint that returns a simple hello world message.
     * This endpoint is publicly accessible and can be used to verify
     * that the backend is running correctly.
     *
     * @return A simple hello world message
     */
    @GetMapping("/")
    public String home() {
        logger.info("Root endpoint accessed");
        return "Hello World! EasyGroup backend is running.";
    }
}