package com.dts.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dts.app.model.Task;
import com.dts.app.model.User;
import com.dts.app.repository.TaskRepository;
import com.dts.app.repository.UserRepository;

import jakarta.validation.Valid;

//Controller class handles Task related API endpoints. Entry point for the frontend app to interact with task data 

//@CrossOriginal allows request from all domains (required due to front and backend having different ports)
@CrossOrigin(origins = "https://applepiandicecream.github.io", allowCredentials = "true") 
@RestController

// mapp all methods in controller to base URL path /api/tasks
@RequestMapping("/api/tasks")
public class TaskController {

    //Dependency injection- create or manage instance of TaskRepository and inject here. 
    //Controller can interact with database without creating repository object itself
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /* Handle HTTP POST requests for create new task 
    endpoint /api/tasks
    @param task - the task object sent in request body (in JSON)
    @Valid- trigger server-side validation checks 
    @return A ResponseEntity- contains new saved Task objecct and HTTP status 201*/
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        task.setUser(user);
        Task savedTask = taskRepository.save(task);
        //return task and 201 status code (CREATED)
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    /* Handle HTTP GET requests- retrieves list of existing tasks */
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Debug : Logged in as : "+ auth.getName());

        String username = auth.getName(); // this is the logged-in username
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        System.out.println("Fetching tasks for User : " + username + "with ID : " + user.getId());

        List<Task> tasks = taskRepository.findByUserId(user.getId());

        System.out.println("Number of tasks found : " + tasks.size());

        return ResponseEntity.ok(tasks);
    }

    
    /* HANDLE PUT update requests */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
        @PathVariable Long id, 
        @Valid @RequestBody Task updatedTask) {
            // get user that is logged in from Srping Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName(); 

            //Fetch user object fron user database
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            //fetch task fron database
            Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));


            //cehck user ID of task for ownership 
            if (!task.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            //update the allowed fields (using the get methods means can maintain old data if not changed)
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setDeadline(updatedTask.getDeadline());
            //save updates to database

            Task saved = taskRepository.save(task);

            //return updated task
            return ResponseEntity.ok(saved);  
        }


    /* HANDLE DELETE delete task requests */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        
        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } 

        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}