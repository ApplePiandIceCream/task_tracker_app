package com.dts.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/** 
 * Class supports database mapping (JPA) ; data transfer object for REST API ; server-side input validation (Jakarta)  
*/
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Title validation constraints
    @NotBlank(message = "Title may not be blank")
    @Size(min = 3, max = 120, message ="Title must be between 3 and 120 characters")
    private String title;

    // description validation constraints 
    @Size(max = 500, message ="Description can be a maximum of 500 characters")
    private String description;

    //status validation constraints (defined in Status.java)
    @NotNull(message = "Please provide task status")
    @Enumerated(EnumType.STRING)
    private Status status;

    //dateTime validation constraints 
   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @FutureOrPresent(message = "Deadline must be today or in the future")
    @NotNull(message = "You must provide a task deadline")
    private LocalDateTime deadline;

    //One to many relationship with User. 
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    //Constructor- JPA: 
    public Task() {
    }

    /**Constructor - create new Task instance 
     * @param title- required- title of task 
     * @param description- optional - description of task 
     * @param status - required- current task status 
     * @param deadline- required - deadline for completion of task 
     * @param user- this is the user object
    */
    public Task(String title, String description, Status status, LocalDateTime deadline, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.user = user;
    }

    //Getters and setters:
    
    /**
     * @return - unique database primary key for this task
     */
    public Long getId() {
        return id;
    }

    /**
     * @return - title of task
     */
    public String getTitle() {
        return title;
    }
    /**
     * @return - description of task
     */
    public String getDescription() {
        return description;
    }
    /**
     * @return - status of the task
     */
    public Status getStatus() {
        return status;
    }
    /**
     * @return - deadline for task
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }
    /**
     * @return - unique database primary key for this taskuser object associated with task
     */
    public User getUser() {
        return user;
    }

    /**
     * @param id - primary key to set- managed by the database
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @param newTitle - set title of task
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
    /**
     * @param newDesc - set description of task (optional)
     */
    public void setDescription(String newDesc) {
        this.description = newDesc;
    }
    /**
     * @param newStatus - set status of task 
     */
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }
    /**
     * @param newDeadline - set deadline of task 
     */
    public void setDeadline(LocalDateTime newDeadline) {
        this.deadline = newDeadline;
    }

    /**
     * @param newUser -set task user 
     */
    public void setUser(User newUser) {
    this.user = newUser;
    }
}

