package com.dts.app.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/** Class supports database mapping (JPA) ; data transfer object for REST API ; server-side input validation (Jakarta)  */
@Entity
@Table(name = "users")

public class User {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Username validation constraint
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 120, message ="Name must be between 3 and 120 characters")
    private String username;

    // password validation constraints 
    @NotBlank(message ="Password cannot be blank")
    @Size(min = 9, message ="Password must be 9 characters minimum")
    private String password;


    //Constructor- JPA: 
    public User() {
    }

    /**Constructor - create new User instance 
     * @param username- required- username created by user 
     * @param password- required - password created by user
    */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //Getters and setters: 
    /**
     * @return- user ID- created by database 
     */
    public Long getId() {
        return id;
    }

    /**
     * @return username of User object
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param id - set user ID 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param newUsername - set Username of user 
     */
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    /** 
     * @param newPassword - set password of User 
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

}

