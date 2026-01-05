package com.dts.app.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dts.app.model.Task;
import com.dts.app.repository.TaskRepository;

import jakarta.validation.Valid;

//Controller class handles Task related API endpoints. Entry point for the frontend app to interact with task data 

//@CrossOriginal allows request from all domains (required due to front and backend having different ports)
@CrossOrigin(origins = "https://applepiandicecream.github.io") 
@RestController

// mapp all methods in controller to base URL path /api/tasks
@RequestMapping("/api/tasks")
public class TaskController {

    //Dependency injection- create or manage instance of TaskRepository and inject here. 
    //Controller can interact with database without creating repository object itself
    @Autowired
    private TaskRepository taskRepository;

    /* Handle HTTP POST requests for create new task 
    endpoint /api/tasks
    @param task - the task object sent in request body (in JSON)
    @Valid- trigger server-side validation checks 
    @return A ResponseEntity- contains new saved Task objecct and HTTP status 201*/
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        //save task object to database via repository 
        Task savedTask = taskRepository.save(task);
        //return task and 201 status code (CREATED)
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    /* Handle HTTP GET requests- retrieves list of existing tasks */
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    
    /* HANDLE PUT update requests */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
        @PathVariable Long id, 
        @Valid @RequestBody Task updatedTask) {
            Optional<Task> existingTask = taskRepository.findById(id);

            if (existingTask.isPresent()) {
                Task task = existingTask.get();
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                task.setDeadline(updatedTask.getDeadline());

                Task saved = taskRepository.save(task);
                return ResponseEntity.ok(saved);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        }
    /* HANDLE DELETE delete task requests */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } 
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}