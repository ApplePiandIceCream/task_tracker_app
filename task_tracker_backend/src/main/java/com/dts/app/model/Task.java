package com.dts.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/** Class supports database mapping (JPA) ; data transfer object for REST API ; server-side input validation (Jakarta)  */
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

    //Constructor- JPA: 
    public Task() {
    }

    /**Constructor - create new Task instance 
     * @param title- required- title of task 
     * @param description- optional - description of task 
     * @param status - required- current task status 
     * @param deadline- required - deadline for completion of task 
    */
    public Task(String title, String description, Status status, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
    }

    //Getters and setters: 
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setDescription(String newDesc) {
        this.description = newDesc;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void setDeadline(LocalDateTime newDeadline) {
        this.deadline = newDeadline;
    }

}

