package com.dts.app;

import com.dts.app.model.Task;
import com.dts.app.model.Status;
import com.dts.app.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dts.app.repository.TaskRepository;
import com.dts.app.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * The main entry point for the Task Tracker Spring Boot application
 * This class is responsible for bootstrapping the Spring context and
 * initialising database
 */

@SpringBootApplication // annotation combines @EnableAutoConfiguration, @ComponentScan adn
                       // @Configuration
public class Application {

    /**
     * Main method - launches the Spring Boot application
     * * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // load the .env file or ignore if missing (needed for deployment)
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
                .ignoreIfMissing()
                .load();
        // copy .env variables into system properties for Spring
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        // start spring app context
        SpringApplication.run(Application.class, args);
    }

    /**
     * Pre-loads database with sample Users and Tasks on startup- only executes if
     * repositories are empty
     * * @param taskRepository - repository for Task persistence
     * 
     * @param userRepository  - repository for User persistence
     * @param passwordEncoder - encoder used to secure sample user passwords
     * @return A {@link CommandLineRunner} that Spring Boot executes after the
     *         context is loaded
     */
    @Bean
    CommandLineRunner initDatabase(TaskRepository taskRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            User testUser1;
            User testUser;
            // initialise test users if none exist
            if (userRepository.count() == 0) {
                // note that usernames must be lowercase for test data to ensure appropriate
                // front end processing

                testUser1 = new User("bob", passwordEncoder.encode("passwordLong123"));
                userRepository.save(testUser1);
                testUser = new User("bill", passwordEncoder.encode("passwordfortest123"));
                userRepository.save(testUser);
            }

            else {
                testUser1 = userRepository.findByUsername("bob")
                        .orElseThrow(() -> new RuntimeException("Bob not found"));
                testUser = userRepository.findByUsername("bill")
                        .orElseThrow(() -> new RuntimeException("Bill not found"));
            }

            // iniitalise tasks if none exist
            if (taskRepository.count() == 0) {
                System.out.println("preloading data...");
                // tasks for 'bob' user
                taskRepository.save(new Task(
                        "Complete Portfolio",
                        "Finish deploying tracker to Render",
                        Status.IN_PROGRESS,
                        LocalDateTime.now().plusDays(1),
                        testUser1));

                taskRepository.save(new Task(
                        "Prep for Interview",
                        "Practice explaining dependencies",
                        Status.PENDING,
                        LocalDateTime.now().plusDays(3),
                        testUser1));

                taskRepository.save(new Task(
                        "Sort application for Wayne Enterprises",
                        "Write cover letter",
                        Status.COMPLETED,
                        LocalDateTime.now().plusDays(10),
                        testUser1));

                taskRepository.save(new Task(
                        "Apply for Stark Industries job",
                        "Need to update CV",
                        Status.AWAITING_RESPONSE,
                        LocalDateTime.now().plusDays(4),
                        testUser1));

                // tasks for 'bill' user
                taskRepository.save(new Task(
                        "Refactor JS for project management app",
                        "Split JS code into separate files and refactor",
                        Status.PENDING,
                        LocalDateTime.now().plusDays(9),
                        testUser));
                taskRepository.save(new Task(
                        "Check to see if user auth is working",
                        "user auth should be restricting task view",
                        Status.IN_PROGRESS,
                        LocalDateTime.now().plusDays(9),
                        testUser));
                taskRepository.save(new Task(
                        "Add authorisiation for task app",
                        "needs auth and login page",
                        Status.AWAITING_RESPONSE,
                        LocalDateTime.now().plusDays(9),
                        testUser));
                taskRepository.save(new Task(
                        "Deploy task app via railway instead of render",
                        "free render deployment too slow for java- add to railway instead",
                        Status.COMPLETED,
                        LocalDateTime.now().plusDays(9),
                        testUser));

                System.out.println("Test data loaded!");
            }
        };
    }

}