package com.dts.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dts.app.model.User;
import java.util.Optional;


@Repository
// JpaRepository<Task, Long>:
// 1. Task: Specifies Entity type this repository manages.
// 2. Long: Specifies data type of the Entity's primary key (id field).
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

}
    
