package com.dts.app;

import com.dts.app.model.Task;
import com.dts.app.model.Status; 
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.dts.app.repository.TaskRepository;


import java.time.LocalDateTime;
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

    //populate with example data

    @Bean
    CommandLineRunner initDatabase(TaskRepository repository) {
        return args -> {
            if(repository.count() == 0) {
                System.out.println("preloading data...");

                repository.save(new Task(
                    "Complete Portfolio", 
                    "Finish deploying tracker to Render",
                    Status.IN_PROGRESS,
                    LocalDateTime.now().plusDays(1)
                ));

                repository.save(new Task(
                    "Prep for Interview", 
                    "Practice explaining dependencies",
                    Status.PENDING,
                    LocalDateTime.now().plusDays(3)
                ));

                repository.save(new Task(
                    "Sort application for Wayne Enterprises", 
                    "Write cover letter",
                    Status.COMPLETED,
                    LocalDateTime.now().plusDays(10)
                ));

                repository.save(new Task(
                    "Apply for Stark Industries job", 
                    "Need to update CV",
                    Status.AWAITING_RESPONSE,
                    LocalDateTime.now().plusDays(4)
                ));

                repository.save(new Task(
                    "Refactor JS for project management app", 
                    "Split JS code into separate files and refactor",
                    Status.PENDING,
                    LocalDateTime.now().plusDays(9)
                ));

                System.out.println("Test data loaded!");
            }
        };
    }
}