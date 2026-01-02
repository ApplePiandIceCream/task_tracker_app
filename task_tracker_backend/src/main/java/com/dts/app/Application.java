package com.dts.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Task Tracker Spring Boot application.
 * This class is responsible for bootstrapping (starting) the Spring context.
 */

@SpringBootApplication // annotation combines @EnableAutoConfiguration, @ComponentScan adn @Configuration
public class Application {

    //main method- starts full application  
    public static void main(String[] args) {
        //start spring app context 
        SpringApplication.run(Application.class, args);
    }
}