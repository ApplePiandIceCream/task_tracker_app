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

/**
 * REST controller for managing tasks 
 * CRUD operationg implementation wtih user- level data isolation 
 */


//@CrossOrigin allows request from all domains (required due to front and backend having different ports)
@CrossOrigin(origins = "https://applepiandicecream.github.io", allowCredentials = "true") 
@RestController

// map all methods in controller to base URL path /api/tasks
@RequestMapping("/api/tasks")
public class TaskController {

    //Dependency injection- create or manage instance of TaskRepository and inject here. 
    //Controller can interact with database without creating repository object itself
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create new tasks and associate it with current user 
     * @param task - the task data validated against constraints 
     * @return ResponseEntity containing the saved Task object and HTTP 201 Status
     * @throws ResponseStatusException 404 if authenticated user record is missing 
     */
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    /**
     * Retrieve tasks belonging to the authenticated user. 
     * Extracts username from JWT stored in SecurityContextHolder,
     * fetches corresponding User ID and queries database for user tasks 
     * @param task
     * @return Response entity containing list of Task objects and HTTP 200 (OK) status
     * @throws ResponseStatusException if authenticated username isn't found 
     */
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

    
    /* HANDLE PUT update requests 
    */
   /** 
    * Updates an existing task after verifying ownership
    * @param id- ID of the task to update 
    * @param updatedTask- the new task data 
    * @return the updated Task object
    * @throws ResponseStatusException 404 if task/user not found, or 403 if user doesn't own task
    */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
        @PathVariable Long id, 
        @Valid @RequestBody Task updatedTask) {
            // get user that is logged in from Srping Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName(); 

            //Fetch user object from user database
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            //fetch task fron database
            Task task = taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

            //check user ID of task for ownership 
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
    /**
     * Deletes task from database after verifying ownership
     * @param id - Id of task to remove 
     * @return HTTP 204 No Content on success 
     * @throws ResponseStatusException 404 if task/user not found, or 403 if user doesn't own task
     */
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