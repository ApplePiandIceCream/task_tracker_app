package com.dts.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dts.app.model.Task;

/**
 * Repository interface for Task entities.
 * * The @Repository annotation marks this interface as a Spring Data repository
 */
@Repository
// JpaRepository<Task, Long>:
// 1. Task: Specifies Entity type this repository manages.
// 2. Long: Specifies data type of the Entity's primary key (id field).
public interface TaskRepository extends JpaRepository<Task, Long> {

}
